package com.bookshopweb.servlet;

import com.bookshopweb.dao.OrderPdfTaskDAO;
import com.bookshopweb.dto.orderPdfTaskDTO;
import com.bookshopweb.utils.JdbiUtils;
import com.google.gson.Gson;
import org.jdbi.v3.core.Jdbi;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/orders/*/pdf-files")
public class GetCompletedOrderPdfServlet extends HttpServlet {
    private OrderPdfTaskDAO taskDAO;

    @Override
    public void init() throws ServletException {
        Jdbi jdbi = JdbiUtils.createInstance();
        taskDAO = new OrderPdfTaskDAO(jdbi);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Lấy phần path sau /api/orders
        String pathInfo = request.getPathInfo(); // ví dụ "/123/pdf-files"
        if (pathInfo == null || pathInfo.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\": \"Thiếu orderId trong URL.\"}");
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            // pathInfo sẽ là dạng "/{orderId}/pdf-files"
            String[] parts = pathInfo.split("/");
            // parts[0] = "" (vì bắt đầu bằng /), parts[1] = orderId, parts[2] = "pdf-files"
            if (parts.length < 3 || !"pdf-files".equals(parts[2])) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"error\": \"Sai định dạng URL.\"}");
                return;
            }

            long orderId = Long.parseLong(parts[1]);

            List<orderPdfTaskDTO> pdfFiles = taskDAO.getCompletedTasksByOrderId(orderId);
            String json = new Gson().toJson(pdfFiles);
            out.print(json);
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"error\": \"orderId không hợp lệ.\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"error\": \"Lỗi server: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}
