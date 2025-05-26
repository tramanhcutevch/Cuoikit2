package com.bookshopweb.servlet;

import com.bookshopweb.dao.OrderPdfTaskDAO;
import org.jdbi.v3.core.Jdbi;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/create-order-pdf")
public class CreateOrderPdfServlet extends HttpServlet {

    private OrderPdfTaskDAO taskDAO;

    @Override
    public void init() throws ServletException {
        // Khởi tạo Jdbi (sửa user/pass nếu cần)
        Jdbi jdbi = Jdbi.create("jdbc:mysql://localhost:3306/bookshopdb", "root", "password");
        taskDAO = new OrderPdfTaskDAO(jdbi);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Long orderId = Long.parseLong(request.getParameter("orderId"));
            Long userId = (Long) request.getSession().getAttribute("userId"); // giả sử userId lưu trong session

            if (userId == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Chưa đăng nhập");
                return;
            }

            // Thêm task vào hàng đợi (DB)
            taskDAO.insertTask(userId, orderId);

            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"PENDING\"}");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Yêu cầu không hợp lệ");
        }
    }
}
