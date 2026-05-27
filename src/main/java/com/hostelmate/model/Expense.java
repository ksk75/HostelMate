package com.hostelmate.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Expense — Model class representing a single expense.
 * 
 * Maps to the 'expenses' table in the database.
 * 
 * @author HostelMate Team
 */
public class Expense implements Serializable {

    private static final long serialVersionUID = 1L;

    // ============================================================
    // Fields
    // ============================================================
    private int       id;
    private String    title;
    private String    description;
    private double    amount;
    private java.sql.Date expenseDate;
    private int       paidBy;         // FK to users.id
    private int       categoryId;     // FK to categories.id
    private String    splitType;      // "EQUAL" or "CUSTOM"
    private boolean   deleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Display fields (joined from other tables)
    private String    paidByName;
    private String    categoryName;
    private String    categoryIcon;
    private int       shareCount;     // Number of people sharing

    // ============================================================
    // Constructors
    // ============================================================

    public Expense() {}

    public Expense(String title, String description, double amount,
                   java.sql.Date expenseDate, int paidBy, int categoryId, String splitType) {
        this.title       = title;
        this.description = description;
        this.amount      = amount;
        this.expenseDate = expenseDate;
        this.paidBy      = paidBy;
        this.categoryId  = categoryId;
        this.splitType   = splitType;
        this.deleted     = false;
    }

    // ============================================================
    // Getters and Setters
    // ============================================================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public java.sql.Date getExpenseDate() { return expenseDate; }
    public void setExpenseDate(java.sql.Date expenseDate) { this.expenseDate = expenseDate; }

    public int getPaidBy() { return paidBy; }
    public void setPaidBy(int paidBy) { this.paidBy = paidBy; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getSplitType() { return splitType; }
    public void setSplitType(String splitType) { this.splitType = splitType; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getPaidByName() { return paidByName; }
    public void setPaidByName(String paidByName) { this.paidByName = paidByName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getCategoryIcon() { return categoryIcon; }
    public void setCategoryIcon(String categoryIcon) { this.categoryIcon = categoryIcon; }

    public int getShareCount() { return shareCount; }
    public void setShareCount(int shareCount) { this.shareCount = shareCount; }

    // ============================================================
    // Convenience Methods
    // ============================================================

    /** Get the per-person share for equal split */
    public double getPerPersonShare() {
        if (shareCount <= 0) return amount;
        return Math.round((amount / shareCount) * 100.0) / 100.0;
    }

    /** Get formatted amount with Rupee symbol */
    public String getFormattedAmount() {
        return String.format("₹%,.2f", amount);
    }

    @Override
    public String toString() {
        return "Expense{id=" + id + ", title='" + title + "', amount=" + amount +
               ", paidBy=" + paidBy + ", category=" + categoryId + "}";
    }
}
