package com.bookshopweb.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;

import org.jdbi.v3.core.Jdbi;

import com.bookshopweb.dao.OrderDAO;
import com.bookshopweb.dao.OrderItemDAO;
import com.bookshopweb.dao.OrderPdfTaskDAO;
import com.bookshopweb.dto.orderPdfTaskDTO;
import com.bookshopweb.item.Order;
import com.bookshopweb.item.OrderItem;
import com.bookshopweb.utils.JdbiUtils;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class OrderPdfTaskWorker implements Runnable {
    private final OrderPdfTaskDAO taskDAO;
    private volatile boolean running = true;
    private final String pdfFolder;

    public OrderPdfTaskWorker(OrderPdfTaskDAO taskDAO) {
        this.taskDAO = taskDAO;

        // Load configuration or use default
        Properties props = new Properties();
        String folderPath;
        try {
            props.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            folderPath = props.getProperty("pdf.storage.path", "C:/pdf/orders/");
        } catch (Exception e) {
            folderPath = "C:/pdf/orders/";
        }
        this.pdfFolder = folderPath;

        // Ensure PDF directory exists
        new File(pdfFolder).mkdirs();
    }

    @Override
    public void run() {
        while (running) {
            try {
                List<orderPdfTaskDTO> pendingTasks = taskDAO.getPendingTasks();

                for (orderPdfTaskDTO task : pendingTasks) {
                    try {
                        String pdfFilePath = generatePdfForOrder(task.getOrderId());
                        taskDAO.updateTaskStatus(task.getId(), "SUCCESS", pdfFilePath);
                    } catch (Exception e) {
                        e.printStackTrace();
                        taskDAO.updateTaskStatus(task.getId(), "FAILED", null);
                    }
                }

                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String generatePdfForOrder(long orderId) throws Exception {
        // Lấy dữ liệu đơn hàng và các sản phẩm
        Jdbi jdbi = JdbiUtils.createInstance();
        OrderDAO orderDAO = jdbi.onDemand(OrderDAO.class);
        OrderItemDAO orderItemDAO = jdbi.onDemand(OrderItemDAO.class);

        Order order = orderDAO.getById(orderId)
                .orElseThrow(() -> new Exception("Không tìm thấy đơn hàng ID = " + orderId));
        List<OrderItem> orderItems = orderItemDAO.getByOrderId(orderId);
        List<String> productNames = orderItemDAO.getProductNamesByOrderId(orderId);

        if (orderItems.isEmpty()) {
            throw new IllegalStateException("Đơn hàng không có sản phẩm nào");
        }

        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String pdfFilePath = pdfFolder + "order_" + orderId + "_" + timestamp + ".pdf";

        Document document = new Document();
        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(pdfFilePath);
            PdfWriter.getInstance(document, fos);
            document.open();

            // Cấu hình font chữ hỗ trợ Unicode
            BaseFont baseFont = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font normalFont = new Font(baseFont, 12, Font.NORMAL);

            // Thêm metadata
            document.addTitle("Hóa đơn đặt hàng #" + orderId);
            document.addCreator("BookShop");

            // Thêm nội dung
            document.add(new Paragraph("HÓA ĐƠN ĐẶT HÀNG", titleFont));
            document.add(new Paragraph("Mã đơn: #" + orderId, normalFont));
            document.add(new Paragraph("Ngày đặt: " + order.getCreatedAt(), normalFont));
            document.add(new Paragraph("Phương thức giao hàng: " + order.getDeliveryMethod(), normalFont));
            document.add(new Paragraph("Phí giao hàng: " + String.format("%,.0f đ", order.getDeliveryPrice()), normalFont));
            document.add(Chunk.NEWLINE);

            // Tạo bảng
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 3, 2, 2, 2});

            // Thêm header
            addTableCell(table, "STT", normalFont);
            addTableCell(table, "Tên sản phẩm", normalFont);
            addTableCell(table, "Số lượng", normalFont);
            addTableCell(table, "Giá bán", normalFont);
            addTableCell(table, "Thành tiền", normalFont);

            double total = 0;

            // Thêm sản phẩm
            for (int i = 0; i < orderItems.size(); i++) {
                OrderItem item = orderItems.get(i);
                String productName = productNames.get(i);
                double lineTotal = (item.getPrice() - item.getDiscount()) * item.getQuantity();
                total += lineTotal;

                addTableCell(table, String.valueOf(i + 1), normalFont);
                addTableCell(table, productName, normalFont);
                addTableCell(table, String.valueOf(item.getQuantity()), normalFont);
                addTableCell(table, String.format("%,.0f đ", item.getPrice()), normalFont);
                addTableCell(table, String.format("%,.0f đ", lineTotal), normalFont);
            }

            document.add(table);
            document.add(Chunk.NEWLINE);

            // Thêm tổng cộng
            document.add(new Paragraph("TỔNG CỘNG: " +
                    String.format("%,.0f đ", (total + order.getDeliveryPrice())), titleFont));

            return pdfFilePath;
        } finally {
            if (document != null && document.isOpen()) {
                document.close();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        table.addCell(cell);
    }

    public void stop() {
        running = false;
    }
}