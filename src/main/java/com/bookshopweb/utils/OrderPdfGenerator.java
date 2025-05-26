package com.bookshopweb.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class OrderPdfGenerator {

    public static void generatePdf(Long orderId, Long userId, String savePath) throws DocumentException, IOException {
        Document document = new Document();

        // Khởi tạo PDF writer
        PdfWriter.getInstance(document, new FileOutputStream(savePath));
        document.open();

        // Tạo nội dung PDF
        document.add(new Paragraph("Đơn hàng #" + orderId));
        document.add(new Paragraph("Người dùng ID: " + userId));
        document.add(new Paragraph("Trạng thái: Đã xử lý"));
        document.add(new Paragraph("Ngày tạo: " +
                java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))));

        document.add(new Paragraph("\nCảm ơn bạn đã đặt hàng tại BookShop!"));

        document.close();
    }
}