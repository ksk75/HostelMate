package com.hostelmate.dao;

import com.hostelmate.model.Expense;
import com.hostelmate.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ExpenseDAO — Data Access Object for Expense operations.
 * 
 * Handles CRUD operations for expenses including:
 * - Adding new expenses
 * - Editing and deleting expenses
 * - Listing expenses with filters
 * - Search functionality
 * 
 * @author HostelMate Team
 */
public class ExpenseDAO {

    // ============================================================
    // SQL Queries
    // ============================================================

    private static final String SQL_INSERT_EXPENSE =
        "INSERT INTO expenses (title, description, amount, expense_date, paid_by, category_id, split_type) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE_EXPENSE =
        "UPDATE expenses SET title = ?, description = ?, amount = ?, expense_date = ?, " +
        "category_id = ?, split_type = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String SQL_SOFT_DELETE =
        "UPDATE expenses SET is_deleted = TRUE, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String SQL_FIND_BY_ID =
        "SELECT e.*, u.full_name AS paid_by_name, c.name AS category_name, c.icon AS category_icon, " +
        "(SELECT COUNT(*) FROM expense_shares es WHERE es.expense_id = e.id) AS share_count " +
        "FROM expenses e " +
        "JOIN users u ON e.paid_by = u.id " +
        "JOIN categories c ON e.category_id = c.id " +
        "WHERE e.id = ? AND e.is_deleted = FALSE";

    private static final String SQL_GET_USER_EXPENSES =
        "SELECT e.*, u.full_name AS paid_by_name, c.name AS category_name, c.icon AS category_icon, " +
        "(SELECT COUNT(*) FROM expense_shares es WHERE es.expense_id = e.id) AS share_count " +
        "FROM expenses e " +
        "JOIN users u ON e.paid_by = u.id " +
        "JOIN categories c ON e.category_id = c.id " +
        "WHERE e.is_deleted = FALSE AND " +
        "(e.paid_by = ? OR e.id IN (SELECT expense_id FROM expense_shares WHERE user_id = ?)) " +
        "ORDER BY e.expense_date DESC, e.created_at DESC";

    private static final String SQL_GET_ALL_EXPENSES =
        "SELECT e.*, u.full_name AS paid_by_name, c.name AS category_name, c.icon AS category_icon, " +
        "(SELECT COUNT(*) FROM expense_shares es WHERE es.expense_id = e.id) AS share_count " +
        "FROM expenses e " +
        "JOIN users u ON e.paid_by = u.id " +
        "JOIN categories c ON e.category_id = c.id " +
        "WHERE e.is_deleted = FALSE " +
        "ORDER BY e.expense_date DESC, e.created_at DESC";

    private static final String SQL_GET_RECENT_EXPENSES =
        "SELECT e.*, u.full_name AS paid_by_name, c.name AS category_name, c.icon AS category_icon, " +
        "(SELECT COUNT(*) FROM expense_shares es WHERE es.expense_id = e.id) AS share_count " +
        "FROM expenses e " +
        "JOIN users u ON e.paid_by = u.id " +
        "JOIN categories c ON e.category_id = c.id " +
        "WHERE e.is_deleted = FALSE AND " +
        "(e.paid_by = ? OR e.id IN (SELECT expense_id FROM expense_shares WHERE user_id = ?)) " +
        "ORDER BY e.created_at DESC LIMIT ?";

    private static final String SQL_SEARCH_EXPENSES =
        "SELECT e.*, u.full_name AS paid_by_name, c.name AS category_name, c.icon AS category_icon, " +
        "(SELECT COUNT(*) FROM expense_shares es WHERE es.expense_id = e.id) AS share_count " +
        "FROM expenses e " +
        "JOIN users u ON e.paid_by = u.id " +
        "JOIN categories c ON e.category_id = c.id " +
        "WHERE e.is_deleted = FALSE AND " +
        "(e.paid_by = ? OR e.id IN (SELECT expense_id FROM expense_shares WHERE user_id = ?)) AND " +
        "(e.title LIKE ? OR e.description LIKE ? OR c.name LIKE ?) " +
        "ORDER BY e.expense_date DESC";

    private static final String SQL_TOTAL_EXPENSES_BY_USER =
        "SELECT COALESCE(SUM(es.share_amount), 0) FROM expense_shares es " +
        "JOIN expenses e ON es.expense_id = e.id " +
        "WHERE es.user_id = ? AND e.is_deleted = FALSE";

    private static final String SQL_TOTAL_PAID_BY_USER =
        "SELECT COALESCE(SUM(e.amount), 0) FROM expenses e " +
        "WHERE e.paid_by = ? AND e.is_deleted = FALSE";

    private static final String SQL_MONTHLY_EXPENSES =
        "SELECT COALESCE(SUM(es.share_amount), 0) FROM expense_shares es " +
        "JOIN expenses e ON es.expense_id = e.id " +
        "WHERE es.user_id = ? AND e.is_deleted = FALSE " +
        "AND MONTH(e.expense_date) = ? AND YEAR(e.expense_date) = ?";

    private static final String SQL_CATEGORY_BREAKDOWN =
        "SELECT c.name, c.icon, COALESCE(SUM(es.share_amount), 0) AS total " +
        "FROM categories c " +
        "LEFT JOIN expenses e ON c.id = e.category_id AND e.is_deleted = FALSE " +
        "LEFT JOIN expense_shares es ON e.id = es.expense_id AND es.user_id = ? " +
        "GROUP BY c.id, c.name, c.icon ORDER BY total DESC";

    private static final String SQL_MONTHLY_TREND =
        "SELECT MONTH(e.expense_date) AS month, YEAR(e.expense_date) AS year, " +
        "COALESCE(SUM(es.share_amount), 0) AS total " +
        "FROM expense_shares es " +
        "JOIN expenses e ON es.expense_id = e.id " +
        "WHERE es.user_id = ? AND e.is_deleted = FALSE " +
        "AND e.expense_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH) " +
        "GROUP BY YEAR(e.expense_date), MONTH(e.expense_date) " +
        "ORDER BY year, month";

    private static final String SQL_TOTAL_ALL_EXPENSES =
        "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE is_deleted = FALSE";

    private static final String SQL_TOTAL_MONTHLY_COLLECTION =
        "SELECT COALESCE(SUM(amount), 0) FROM expenses " +
        "WHERE is_deleted = FALSE AND MONTH(expense_date) = MONTH(CURDATE()) " +
        "AND YEAR(expense_date) = YEAR(CURDATE())";

    // ============================================================
    // CRUD Operations
    // ============================================================

    /**
     * Add a new expense.
     * 
     * @param expense the expense to add
     * @return generated expense ID, or -1 on failure
     */
    public int addExpense(Expense expense) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_EXPENSE, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, expense.getTitle());
            stmt.setString(2, expense.getDescription());
            stmt.setDouble(3, expense.getAmount());
            stmt.setDate(4, expense.getExpenseDate());
            stmt.setInt(5, expense.getPaidBy());
            stmt.setInt(6, expense.getCategoryId());
            stmt.setString(7, expense.getSplitType());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error adding expense: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Update an existing expense.
     * 
     * @param expense the expense with updated fields
     * @return true if updated
     */
    public boolean updateExpense(Expense expense) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_EXPENSE)) {

            stmt.setString(1, expense.getTitle());
            stmt.setString(2, expense.getDescription());
            stmt.setDouble(3, expense.getAmount());
            stmt.setDate(4, expense.getExpenseDate());
            stmt.setInt(5, expense.getCategoryId());
            stmt.setString(6, expense.getSplitType());
            stmt.setInt(7, expense.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error updating expense: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Soft-delete an expense (set is_deleted = true).
     * 
     * @param expenseId the expense ID
     * @return true if deleted
     */
    public boolean deleteExpense(int expenseId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SOFT_DELETE)) {

            stmt.setInt(1, expenseId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error deleting expense: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Find an expense by ID.
     * 
     * @param expenseId the expense ID
     * @return Expense object, or null
     */
    public Expense findById(int expenseId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {

            stmt.setInt(1, expenseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToExpense(rs);
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error finding expense: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all expenses for a user (paid by or shared with).
     * 
     * @param userId the user's ID
     * @return list of expenses
     */
    public List<Expense> getUserExpenses(int userId) {
        List<Expense> expenses = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_USER_EXPENSES)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expenses.add(mapResultSetToExpense(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error getting user expenses: " + e.getMessage());
            e.printStackTrace();
        }
        return expenses;
    }

    /**
     * Get all expenses (for admin view).
     * 
     * @return list of all expenses
     */
    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_ALL_EXPENSES)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expenses.add(mapResultSetToExpense(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error getting all expenses: " + e.getMessage());
            e.printStackTrace();
        }
        return expenses;
    }

    /**
     * Get recent expenses for a user (for dashboard).
     * 
     * @param userId the user's ID
     * @param limit  max number of expenses to return
     * @return list of recent expenses
     */
    public List<Expense> getRecentExpenses(int userId, int limit) {
        List<Expense> expenses = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_RECENT_EXPENSES)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expenses.add(mapResultSetToExpense(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error getting recent expenses: " + e.getMessage());
            e.printStackTrace();
        }
        return expenses;
    }

    /**
     * Search expenses by keyword.
     * 
     * @param userId  the user's ID
     * @param keyword the search term
     * @return matching expenses
     */
    public List<Expense> searchExpenses(int userId, String keyword) {
        List<Expense> expenses = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SEARCH_EXPENSES)) {

            String searchTerm = "%" + keyword + "%";
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setString(3, searchTerm);
            stmt.setString(4, searchTerm);
            stmt.setString(5, searchTerm);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expenses.add(mapResultSetToExpense(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error searching expenses: " + e.getMessage());
            e.printStackTrace();
        }
        return expenses;
    }

    /**
     * Get expenses filtered by category and/or date range.
     * 
     * @param userId     the user's ID
     * @param categoryId category filter (0 for all)
     * @param startDate  start date filter (null for no start)
     * @param endDate    end date filter (null for no end)
     * @return filtered expenses
     */
    public List<Expense> getFilteredExpenses(int userId, int categoryId,
                                              java.sql.Date startDate, java.sql.Date endDate) {
        List<Expense> expenses = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT e.*, u.full_name AS paid_by_name, c.name AS category_name, c.icon AS category_icon, " +
            "(SELECT COUNT(*) FROM expense_shares es WHERE es.expense_id = e.id) AS share_count " +
            "FROM expenses e " +
            "JOIN users u ON e.paid_by = u.id " +
            "JOIN categories c ON e.category_id = c.id " +
            "WHERE e.is_deleted = FALSE AND " +
            "(e.paid_by = ? OR e.id IN (SELECT expense_id FROM expense_shares WHERE user_id = ?)) "
        );

        List<Object> params = new ArrayList<>();
        params.add(userId);
        params.add(userId);

        if (categoryId > 0) {
            sql.append("AND e.category_id = ? ");
            params.add(categoryId);
        }
        if (startDate != null) {
            sql.append("AND e.expense_date >= ? ");
            params.add(startDate);
        }
        if (endDate != null) {
            sql.append("AND e.expense_date <= ? ");
            params.add(endDate);
        }

        sql.append("ORDER BY e.expense_date DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof java.sql.Date) {
                    stmt.setDate(i + 1, (java.sql.Date) param);
                }
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                expenses.add(mapResultSetToExpense(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error filtering expenses: " + e.getMessage());
            e.printStackTrace();
        }
        return expenses;
    }

    // ============================================================
    // Analytics Queries
    // ============================================================

    /** Get total expense amount for a user (sum of all shares) */
    public double getTotalExpensesByUser(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_TOTAL_EXPENSES_BY_USER)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error getting total: " + e.getMessage());
        }
        return 0.0;
    }

    /** Get total amount paid by a user */
    public double getTotalPaidByUser(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_TOTAL_PAID_BY_USER)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error getting total paid: " + e.getMessage());
        }
        return 0.0;
    }

    /** Get monthly expense total for a user */
    public double getMonthlyExpenses(int userId, int month, int year) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_MONTHLY_EXPENSES)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, month);
            stmt.setInt(3, year);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error getting monthly expenses: " + e.getMessage());
        }
        return 0.0;
    }

    /** Get category-wise expense breakdown for a user */
    public List<Object[]> getCategoryBreakdown(int userId) {
        List<Object[]> breakdown = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_CATEGORY_BREAKDOWN)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                breakdown.add(new Object[]{
                    rs.getString("name"),
                    rs.getString("icon"),
                    rs.getDouble("total")
                });
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error getting category breakdown: " + e.getMessage());
        }
        return breakdown;
    }

    /** Get monthly trend data (last 6 months) */
    public List<Object[]> getMonthlyTrend(int userId) {
        List<Object[]> trend = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_MONTHLY_TREND)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                trend.add(new Object[]{
                    rs.getInt("month"),
                    rs.getInt("year"),
                    rs.getDouble("total")
                });
            }
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error getting monthly trend: " + e.getMessage());
        }
        return trend;
    }

    /** Get total expenses across all users (admin) */
    public double getTotalAllExpenses() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_TOTAL_ALL_EXPENSES)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error getting total all expenses: " + e.getMessage());
        }
        return 0.0;
    }

    /** Get current month's total collection (admin) */
    public double getMonthlyCollection() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_TOTAL_MONTHLY_COLLECTION)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            System.err.println("[ExpenseDAO] Error getting monthly collection: " + e.getMessage());
        }
        return 0.0;
    }

    // ============================================================
    // Helper Methods
    // ============================================================

    private Expense mapResultSetToExpense(ResultSet rs) throws SQLException {
        Expense expense = new Expense();
        expense.setId(rs.getInt("id"));
        expense.setTitle(rs.getString("title"));
        expense.setDescription(rs.getString("description"));
        expense.setAmount(rs.getDouble("amount"));
        expense.setExpenseDate(rs.getDate("expense_date"));
        expense.setPaidBy(rs.getInt("paid_by"));
        expense.setCategoryId(rs.getInt("category_id"));
        expense.setSplitType(rs.getString("split_type"));
        expense.setDeleted(rs.getBoolean("is_deleted"));
        expense.setCreatedAt(rs.getTimestamp("created_at"));
        expense.setUpdatedAt(rs.getTimestamp("updated_at"));

        // Joined fields
        try {
            expense.setPaidByName(rs.getString("paid_by_name"));
            expense.setCategoryName(rs.getString("category_name"));
            expense.setCategoryIcon(rs.getString("category_icon"));
            expense.setShareCount(rs.getInt("share_count"));
        } catch (SQLException e) {
            // Columns not in result set
        }

        return expense;
    }
}
