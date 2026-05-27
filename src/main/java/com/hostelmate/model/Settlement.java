package com.hostelmate.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Settlement — Model class for balance settlements between users.
 * Maps to the 'settlements' table.
 * 
 * @author HostelMate Team
 */
public class Settlement implements Serializable {

    private static final long serialVersionUID = 1L;

    private int       id;
    private int       fromUserId;
    private int       toUserId;
    private double    amount;
    private java.sql.Date settledDate;
    private String    status;     // "PENDING", "COMPLETED", "CANCELLED"
    private String    notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Display fields
    private String    fromUserName;
    private String    toUserName;

    // ============================================================
    // Constructors
    // ============================================================
    public Settlement() {}

    public Settlement(int fromUserId, int toUserId, double amount, String notes) {
        this.fromUserId = fromUserId;
        this.toUserId   = toUserId;
        this.amount     = amount;
        this.notes      = notes;
        this.status     = "PENDING";
    }

    // ============================================================
    // Getters and Setters
    // ============================================================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getFromUserId() { return fromUserId; }
    public void setFromUserId(int fromUserId) { this.fromUserId = fromUserId; }

    public int getToUserId() { return toUserId; }
    public void setToUserId(int toUserId) { this.toUserId = toUserId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public java.sql.Date getSettledDate() { return settledDate; }
    public void setSettledDate(java.sql.Date settledDate) { this.settledDate = settledDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getFromUserName() { return fromUserName; }
    public void setFromUserName(String fromUserName) { this.fromUserName = fromUserName; }

    public String getToUserName() { return toUserName; }
    public void setToUserName(String toUserName) { this.toUserName = toUserName; }

    public boolean isPending()   { return "PENDING".equals(status); }
    public boolean isCompleted() { return "COMPLETED".equals(status); }

    public String getFormattedAmount() {
        return String.format("₹%,.2f", amount);
    }

    @Override
    public String toString() {
        return "Settlement{id=" + id + ", from=" + fromUserId + ", to=" + toUserId +
               ", amount=" + amount + ", status='" + status + "'}";
    }
}
