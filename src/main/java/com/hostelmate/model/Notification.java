package com.hostelmate.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Notification — Model class for in-app notifications.
 * Maps to the 'notifications' table.
 * 
 * @author HostelMate Team
 */
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    private int       id;
    private int       userId;
    private String    message;
    private String    type;      // EXPENSE_ADDED, PAYMENT_RECEIVED, etc.
    private String    link;
    private boolean   read;
    private Timestamp createdAt;

    // ============================================================
    // Constructors
    // ============================================================
    public Notification() {}

    public Notification(int userId, String message, String type) {
        this.userId  = userId;
        this.message = message;
        this.type    = type;
        this.read    = false;
    }

    public Notification(int userId, String message, String type, String link) {
        this(userId, message, type);
        this.link = link;
    }

    // ============================================================
    // Getters and Setters
    // ============================================================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /** Get a Bootstrap icon class based on notification type */
    public String getIconClass() {
        switch (type != null ? type : "") {
            case "EXPENSE_ADDED":       return "bi-receipt text-primary";
            case "PAYMENT_RECEIVED":    return "bi-check-circle text-success";
            case "PAYMENT_REMINDER":    return "bi-bell text-warning";
            case "SETTLEMENT_REQUEST":  return "bi-arrow-left-right text-info";
            case "RENT_REMINDER":       return "bi-house-door text-danger";
            default:                    return "bi-info-circle text-secondary";
        }
    }

    /** Get time ago string for display */
    public String getTimeAgo() {
        if (createdAt == null) return "";
        long diffMs = System.currentTimeMillis() - createdAt.getTime();
        long diffMins  = diffMs / (1000 * 60);
        long diffHours = diffMs / (1000 * 60 * 60);
        long diffDays  = diffMs / (1000 * 60 * 60 * 24);

        if (diffMins < 1)   return "just now";
        if (diffMins < 60)  return diffMins + " min ago";
        if (diffHours < 24) return diffHours + " hr ago";
        if (diffDays < 7)   return diffDays + " day" + (diffDays > 1 ? "s" : "") + " ago";
        return new java.text.SimpleDateFormat("dd MMM").format(createdAt);
    }

    @Override
    public String toString() {
        return "Notification{id=" + id + ", userId=" + userId + 
               ", type='" + type + "', read=" + read + "}";
    }
}
