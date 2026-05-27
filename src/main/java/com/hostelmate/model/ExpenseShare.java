package com.hostelmate.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * ExpenseShare — Model class for individual expense shares.
 * Maps to the 'expense_shares' table.
 * Tracks how much each user owes for a shared expense.
 * 
 * @author HostelMate Team
 */
public class ExpenseShare implements Serializable {

    private static final long serialVersionUID = 1L;

    private int       id;
    private int       expenseId;
    private int       userId;
    private double    shareAmount;
    private boolean   paid;
    private java.sql.Date paidDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Display fields (joined)
    private String    userName;
    private String    expenseTitle;

    // ============================================================
    // Constructors
    // ============================================================
    public ExpenseShare() {}

    public ExpenseShare(int expenseId, int userId, double shareAmount) {
        this.expenseId   = expenseId;
        this.userId      = userId;
        this.shareAmount = shareAmount;
        this.paid        = false;
    }

    // ============================================================
    // Getters and Setters
    // ============================================================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getExpenseId() { return expenseId; }
    public void setExpenseId(int expenseId) { this.expenseId = expenseId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getShareAmount() { return shareAmount; }
    public void setShareAmount(double shareAmount) { this.shareAmount = shareAmount; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    public java.sql.Date getPaidDate() { return paidDate; }
    public void setPaidDate(java.sql.Date paidDate) { this.paidDate = paidDate; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getExpenseTitle() { return expenseTitle; }
    public void setExpenseTitle(String expenseTitle) { this.expenseTitle = expenseTitle; }

    /** Get formatted share amount */
    public String getFormattedAmount() {
        return String.format("₹%,.2f", shareAmount);
    }

    @Override
    public String toString() {
        return "ExpenseShare{id=" + id + ", expenseId=" + expenseId +
               ", userId=" + userId + ", amount=" + shareAmount + ", paid=" + paid + "}";
    }
}
