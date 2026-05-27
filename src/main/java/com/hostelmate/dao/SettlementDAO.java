package com.hostelmate.dao;

import com.hostelmate.model.Settlement;
import com.hostelmate.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SettlementDAO — Data Access Object for balance settlements.
 * 
 * Tracks settlements between users (who paid whom to clear balances).
 * 
 * @author HostelMate Team
 */
public class SettlementDAO {

    private static final String SQL_INSERT =
        "INSERT INTO settlements (from_user_id, to_user_id, amount, status, notes) " +
        "VALUES (?, ?, ?, 'PENDING', ?)";

    private static final String SQL_COMPLETE =
        "UPDATE settlements SET status = 'COMPLETED', settled_date = CURDATE(), " +
        "updated_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String SQL_CANCEL =
        "UPDATE settlements SET status = 'CANCELLED', updated_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String SQL_GET_USER_SETTLEMENTS =
        "SELECT s.*, fu.full_name AS from_user_name, tu.full_name AS to_user_name " +
        "FROM settlements s " +
        "JOIN users fu ON s.from_user_id = fu.id " +
        "JOIN users tu ON s.to_user_id = tu.id " +
        "WHERE s.from_user_id = ? OR s.to_user_id = ? " +
        "ORDER BY s.created_at DESC";

    private static final String SQL_GET_PENDING =
        "SELECT s.*, fu.full_name AS from_user_name, tu.full_name AS to_user_name " +
        "FROM settlements s " +
        "JOIN users fu ON s.from_user_id = fu.id " +
        "JOIN users tu ON s.to_user_id = tu.id " +
        "WHERE (s.from_user_id = ? OR s.to_user_id = ?) AND s.status = 'PENDING' " +
        "ORDER BY s.created_at DESC";

    private static final String SQL_FIND_BY_ID =
        "SELECT s.*, fu.full_name AS from_user_name, tu.full_name AS to_user_name " +
        "FROM settlements s " +
        "JOIN users fu ON s.from_user_id = fu.id " +
        "JOIN users tu ON s.to_user_id = tu.id " +
        "WHERE s.id = ?";

    private static final String SQL_GET_ALL =
        "SELECT s.*, fu.full_name AS from_user_name, tu.full_name AS to_user_name " +
        "FROM settlements s " +
        "JOIN users fu ON s.from_user_id = fu.id " +
        "JOIN users tu ON s.to_user_id = tu.id " +
        "ORDER BY s.created_at DESC";

    // ============================================================
    // CRUD Operations
    // ============================================================

    /**
     * Create a new settlement request.
     * 
     * @param settlement the settlement to create
     * @return generated ID, or -1
     */
    public int createSettlement(Settlement settlement) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, settlement.getFromUserId());
            stmt.setInt(2, settlement.getToUserId());
            stmt.setDouble(3, settlement.getAmount());
            stmt.setString(4, settlement.getNotes());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[SettlementDAO] Error creating settlement: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Mark a settlement as completed.
     * 
     * @param settlementId the settlement ID
     * @return true if updated
     */
    public boolean completeSettlement(int settlementId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COMPLETE)) {
            stmt.setInt(1, settlementId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[SettlementDAO] Error completing settlement: " + e.getMessage());
        }
        return false;
    }

    /**
     * Cancel a settlement.
     * 
     * @param settlementId the settlement ID
     * @return true if updated
     */
    public boolean cancelSettlement(int settlementId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_CANCEL)) {
            stmt.setInt(1, settlementId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[SettlementDAO] Error cancelling settlement: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get all settlements for a user.
     * 
     * @param userId the user's ID
     * @return list of settlements
     */
    public List<Settlement> getUserSettlements(int userId) {
        List<Settlement> settlements = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_USER_SETTLEMENTS)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                settlements.add(mapResultSetToSettlement(rs));
            }
        } catch (SQLException e) {
            System.err.println("[SettlementDAO] Error getting settlements: " + e.getMessage());
        }
        return settlements;
    }

    /**
     * Get pending settlements for a user.
     */
    public List<Settlement> getPendingSettlements(int userId) {
        List<Settlement> settlements = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_PENDING)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                settlements.add(mapResultSetToSettlement(rs));
            }
        } catch (SQLException e) {
            System.err.println("[SettlementDAO] Error getting pending settlements: " + e.getMessage());
        }
        return settlements;
    }

    /**
     * Find a settlement by ID.
     */
    public Settlement findById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSettlement(rs);
            }
        } catch (SQLException e) {
            System.err.println("[SettlementDAO] Error finding settlement: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get all settlements (admin).
     */
    public List<Settlement> getAllSettlements() {
        List<Settlement> settlements = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_ALL)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                settlements.add(mapResultSetToSettlement(rs));
            }
        } catch (SQLException e) {
            System.err.println("[SettlementDAO] Error getting all settlements: " + e.getMessage());
        }
        return settlements;
    }

    // ============================================================
    // Helper
    // ============================================================

    private Settlement mapResultSetToSettlement(ResultSet rs) throws SQLException {
        Settlement s = new Settlement();
        s.setId(rs.getInt("id"));
        s.setFromUserId(rs.getInt("from_user_id"));
        s.setToUserId(rs.getInt("to_user_id"));
        s.setAmount(rs.getDouble("amount"));
        s.setSettledDate(rs.getDate("settled_date"));
        s.setStatus(rs.getString("status"));
        s.setNotes(rs.getString("notes"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));

        try {
            s.setFromUserName(rs.getString("from_user_name"));
            s.setToUserName(rs.getString("to_user_name"));
        } catch (SQLException e) { /* columns not present */ }

        return s;
    }
}
