package com.bookshopweb.dao;

import com.bookshopweb.dto.orderPdfTaskDTO;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public class OrderPdfTaskDAO {
    private final Jdbi jdbi;

    public OrderPdfTaskDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    // Thêm task mới với status = 'PENDING' và createdAt = NOW()
    public void insertTask(Long userId, Long orderId) {
        String sql = "INSERT INTO order_pdf_task (userId, orderId, status, createdAt) VALUES (:userId, :orderId, 'PENDING', NOW())";
        jdbi.useHandle(handle ->
                handle.createUpdate(sql)
                        .bind("userId", userId)
                        .bind("orderId", orderId)
                        .execute()
        );
    }

    // Lấy danh sách task có status = 'PENDING'
    public List<orderPdfTaskDTO> getPendingTasks() {
        String sql = "SELECT * FROM order_pdf_task WHERE status = 'PENDING'";
        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .map((rs, ctx) -> {
                            orderPdfTaskDTO task = new orderPdfTaskDTO();
                            task.setId(rs.getLong("id"));
                            task.setUserId(rs.getLong("userId"));
                            task.setOrderId(rs.getLong("orderId"));
                            task.setStatus(rs.getString("status"));
                            task.setFilePath(rs.getString("filePath"));
                            // Chuyển Timestamp sang LocalDateTime
                            if (rs.getTimestamp("createdAt") != null)
                                task.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                            if (rs.getTimestamp("updatedAt") != null)
                                task.setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
                            return task;
                        })
                        .list()
        );
    }
    // Lấy danh sách các task đã hoàn thành theo orderId (status = 'SUCCESS')
    public List<orderPdfTaskDTO> getCompletedTasksByOrderId(Long orderId) {
        String sql = "SELECT * FROM order_pdf_task WHERE orderId = :orderId AND status = 'SUCCESS'";
        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .bind("orderId", orderId)
                        .map((rs, ctx) -> {
                            orderPdfTaskDTO task = new orderPdfTaskDTO();
                            task.setId(rs.getLong("id"));
                            task.setUserId(rs.getLong("userId"));
                            task.setOrderId(rs.getLong("orderId"));
                            task.setStatus(rs.getString("status"));
                            task.setFilePath(rs.getString("filePath"));
                            if (rs.getTimestamp("createdAt") != null)
                                task.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                            if (rs.getTimestamp("updatedAt") != null)
                                task.setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
                            return task;
                        })
                        .list()
        );
    }

    // Cập nhật trạng thái task, filePath, updatedAt
    public void updateTaskStatus(Long taskId, String status, String filePath) {
        String sql = "UPDATE order_pdf_task SET status = :status, filePath = :filePath, updatedAt = NOW() WHERE id = :taskId";
        jdbi.useHandle(handle ->
                handle.createUpdate(sql)
                        .bind("status", status)
                        .bind("filePath", filePath)
                        .bind("taskId", taskId)
                        .execute()
        );
    }
    // Lấy danh sách các task đã hoàn thành của user (status = 'SUCCESS')
    public List<orderPdfTaskDTO> getCompletedTasksByUser(Long userId) {
        String sql = "SELECT * FROM order_pdf_task WHERE userId = :userId AND status = 'SUCCESS'";
        return jdbi.withHandle(handle ->
                handle.createQuery(sql)
                        .bind("userId", userId)
                        .map((rs, ctx) -> {
                            orderPdfTaskDTO task = new orderPdfTaskDTO();
                            task.setId(rs.getLong("id"));
                            task.setUserId(rs.getLong("userId"));
                            task.setOrderId(rs.getLong("orderId"));
                            task.setStatus(rs.getString("status"));
                            task.setFilePath(rs.getString("filePath"));
                            if (rs.getTimestamp("createdAt") != null)
                                task.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                            if (rs.getTimestamp("updatedAt") != null)
                                task.setUpdatedAt(rs.getTimestamp("updatedAt").toLocalDateTime());
                            return task;
                        })
                        .list()
        );
    }
}

