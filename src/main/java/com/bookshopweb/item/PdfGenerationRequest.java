package com.bookshopweb.item;

import java.util.Date;

public class PdfGenerationRequest {
    private long id;
    private long orderId;
    private long userId;
    private String status; // PENDING, PROCESSING, DONE, FAILED
    private String filePath;
    private Date createdAt;
    private Date updatedAt;

    public PdfGenerationRequest() {}

    public PdfGenerationRequest(long id, long orderId, long userId, String status, String filePath, Date createdAt, Date updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.filePath = filePath;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
