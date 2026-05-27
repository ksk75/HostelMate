package com.hostelmate.dao;

import com.hostelmate.model.ExpenseShare;
import com.hostelmate.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExpenseShareDAO — Data Access Object for expense sharing.
 * 
 * Handles:
 * - Creating shares when an expense is added
 * - Calculating who owes whom
 * - Computing net balances between users
 * - Marking shares as paid
 * 
 * @author HostelMate Team
 */
public class ExpenseShareDAO {

    private static final String SQL_INSERT_SHARE =
        "INSERT INTO expense_shares (expense_id, user_id, share_amount, is_paid) VALUES (?, ?, ?, ?)";

    private static final String SQL_GET_SHARES_BY_EXPENSE =
        "SELECT es.*, u.full_name AS user_name " +
        "FROM expense_shares es " +
        "JOIN users u ON es.user_id = u.id " +
        "WHERE es.expense_id = ? ORDER BY u.full_name";

    private static final String SQL_GET_USER_PENDING_SHARES =
        "SELECT es.*, u.full_name AS user_name, e.title AS expense_title " +
        "FROM expense_shares es " +
        "JOIN users u ON es.user_id = u.id " +
        "JOIN expenses e ON es.expense_id = e.id " +
        "WHERE es.user_id = ? AND es.is_paid = FALSE AND e.is_deleted = FALSE " +
        "AND e.paid_by <> ? " +
        "ORDER BY e.expense_date DESC";

    private static final String SQL_MARK_PAID =
        "UPDATE expense_shares SET is_paid = TRUE, paid_date = CURDATE(), " +
        "updated_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String SQL_DELETE_BY_EXPENSE =
        "DELETE FROM expense_shares WHERE expense_id = ?";

    private static final String SQL_PENDING_AMOUNT =
        "SELECT COALESCE(SUM(es.share_amount), 0) FROM expense_shares es " +
        "JOIN expenses e ON es.expense_id = e.id " +
        "WHERE es.user_id = ? AND es.is_paid = FALSE AND e.is_deleted = FALSE " +
        "AND e.paid_by <> ?";

    private static final String SQL_PAID_AMOUNT =
        "SELECT COALESCE(SUM(es.share_amount), 0) FROM expense_shares es " +
        "JOIN expenses e ON es.expense_id = e.id " +
        "WHERE es.user_id = ? AND es.is_paid = TRUE AND e.is_deleted = FALSE";

    /**
     * Net balance query: For each pair (payer, sharer), calculate how much
     * the sharer owes the payer for unpaid shares.
     */
    private static final String SQL_NET_BALANCES =
        "SELECT e.paid_by AS creditor_id, cu.full_name AS creditor_name, " +
        "es.user_id AS debtor_id, du.full_name AS debtor_name, " +
        "SUM(es.share_amount) AS total_owed " +
        "FROM expense_shares es " +
        "JOIN expenses e ON es.expense_id = e.id " +
        "JOIN users cu ON e.paid_by = cu.id " +
        "JOIN users du ON es.user_id = du.id " +
        "WHERE es.is_paid = FALSE AND e.is_deleted = FALSE " +
        "AND e.paid_by <> es.user_id " +
        "AND (es.user_id = ? OR e.paid_by = ?) " +
        "GROUP BY e.paid_by, es.user_id, cu.full_name, du.full_name";

    private static final String SQL_ALL_PENDING_SHARES =
        "SELECT COALESCE(SUM(es.share_amount), 0) FROM expense_shares es " +
        "JOIN expenses e ON es.expense_id = e.id " +
        "WHERE es.is_paid = FALSE AND e.is_deleted = FALSE AND e.paid_by <> es.user_id";

    // ============================================================
    // CRUD Operations
    // ============================================================

    /**
     * Add expense shares for an expense.
     * 
     * @param expenseId  the expense ID
     * @param paidBy     the user who paid
     * @param userIds    array of user IDs sharing the expense
     * @param amounts    array of share amounts (parallel with userIds)
     * @return true if all shares inserted
     */
    public boolean addShares(int expenseId, int paidBy, int[] userIds, double[] amounts) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_SHARE)) {

            for (int i = 0; i < userIds.length; i++) {
                stmt.setInt(1, expenseId);
                stmt.setInt(2, userIds[i]);
                stmt.setDouble(3, amounts[i]);
                // The payer's own share is auto-paid
                stmt.setBoolean(4, userIds[i] == paidBy);
                stmt.addBatch();
            }

            int[] results = stmt.executeBatch();
            for (int r : results) {
                if (r <= 0 && r != Statement.SUCCESS_NO_INFO) return false;
            }
            return true;
        } catch (SQLException e) {
            System.err.println("[ExpenseShareDAO] Error adding shares: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get all shares for an expense.
     * 
     * @param expenseId the expense ID
     * @return list of expense shares
     */
    public List<ExpenseShare> getSharesByExpense(int expenseId) {
        List<ExpenseShare> shares = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_SHARES_BY_EXPENSE)) {

            stmt.setInt(1, expenseId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                shares.add(mapResultSetToShare(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseShareDAO] Error getting shares: " + e.getMessage());
        }
        return shares;
    }

    /**
     * Get all pending (unpaid) shares for a user.
     * Only shows shares where someone else paid.
     * 
     * @param userId the user's ID
     * @return list of pending shares
     */
    public List<ExpenseShare> getPendingShares(int userId) {
        List<ExpenseShare> shares = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_USER_PENDING_SHARES)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ExpenseShare share = mapResultSetToShare(rs);
                try {
                    share.setExpenseTitle(rs.getString("expense_title"));
                } catch (SQLException ex) { /* column not present */ }
                shares.add(share);
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseShareDAO] Error getting pending shares: " + e.getMessage());
        }
        return shares;
    }

    /**
     * Mark a share as paid.
     * 
     * @param shareId the share ID
     * @return true if updated
     */
    public boolean markAsPaid(int shareId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_MARK_PAID)) {

            stmt.setInt(1, shareId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ExpenseShareDAO] Error marking paid: " + e.getMessage());
        }
        return false;
    }

    /**
     * Delete all shares for an expense (used when updating splits).
     * 
     * @param expenseId the expense ID
     * @return true if deleted
     */
    public boolean deleteSharesByExpense(int expenseId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_BY_EXPENSE)) {

            stmt.setInt(1, expenseId);
            return stmt.executeUpdate() >= 0; // 0 is ok if no shares
        } catch (SQLException e) {
            System.err.println("[ExpenseShareDAO] Error deleting shares: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get total pending (unpaid) amount for a user.
     * 
     * @param userId the user's ID
     * @return pending amount
     */
    public double getPendingAmount(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_PENDING_AMOUNT)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[ExpenseShareDAO] Error getting pending amount: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Get total paid amount for a user (across all settled shares).
     * 
     * @param userId the user's ID
     * @return paid amount
     */
    public double getPaidAmount(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_PAID_AMOUNT)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[ExpenseShareDAO] Error getting paid amount: " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Calculate net balances for a user.
     * Returns a map: other_user_id -> net_amount
     * Positive = they owe you; Negative = you owe them.
     * 
     * @param userId the user's ID
     * @return list of balance entries [userId, userName, netAmount]
     */
    public List<Object[]> getNetBalances(int userId) {
        List<Object[]> balances = new ArrayList<>();
        Map<Integer, Object[]> balanceMap = new HashMap<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_NET_BALANCES)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int creditorId = rs.getInt("creditor_id");
                int debtorId   = rs.getInt("debtor_id");
                double owed    = rs.getDouble("total_owed");

                if (creditorId == userId) {
                    // Someone owes ME
                    Object[] entry = balanceMap.getOrDefault(debtorId, 
                        new Object[]{debtorId, rs.getString("debtor_name"), 0.0});
                    entry[2] = (double) entry[2] + owed;
                    balanceMap.put(debtorId, entry);
                } else if (debtorId == userId) {
                    // I owe someone
                    Object[] entry = balanceMap.getOrDefault(creditorId, 
                        new Object[]{creditorId, rs.getString("creditor_name"), 0.0});
                    entry[2] = (double) entry[2] - owed;
                    balanceMap.put(creditorId, entry);
                }
            }

            balances.addAll(balanceMap.values());
        } catch (SQLException e) {
            System.err.println("[ExpenseShareDAO] Error getting net balances: " + e.getMessage());
        }
        return balances;
    }

    /**
     * Get total pending amount across all users (admin).
     * 
     * @return total pending amount
     */
    public double getTotalPendingAll() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_ALL_PENDING_SHARES)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[ExpenseShareDAO] Error getting total pending: " + e.getMessage());
        }
        return 0.0;
    }

    // ============================================================
    // Helper Methods
    // ============================================================

    private ExpenseShare mapResultSetToShare(ResultSet rs) throws SQLException {
        ExpenseShare share = new ExpenseShare();
        share.setId(rs.getInt("id"));
        share.setExpenseId(rs.getInt("expense_id"));
        share.setUserId(rs.getInt("user_id"));
        share.setShareAmount(rs.getDouble("share_amount"));
        share.setPaid(rs.getBoolean("is_paid"));
        share.setPaidDate(rs.getDate("paid_date"));
        share.setCreatedAt(rs.getTimestamp("created_at"));
        share.setUpdatedAt(rs.getTimestamp("updated_at"));

        try {
            share.setUserName(rs.getString("user_name"));
        } catch (SQLException e) { /* column not in result */ }

        return share;
    }
}
