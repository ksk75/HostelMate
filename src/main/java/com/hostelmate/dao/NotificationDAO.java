package com.hostelmate.dao;

import com.hostelmate.model.Notification;
import com.hostelmate.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * NotificationDAO — Data Access Object for notifications.
 * 
 * @author HostelMate Team
 */
public class NotificationDAO {

    private static final String SQL_INSERT =
        "INSERT INTO notifications (user_id, message, type, link) VALUES (?, ?, ?, ?)";

    private static final String SQL_GET_USER_NOTIFICATIONS =
        "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";

    private static final String SQL_GET_UNREAD_COUNT =
        "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";

    private static final String SQL_MARK_READ =
        "UPDATE notifications SET is_read = TRUE WHERE id = ?";

    private static final String SQL_MARK_ALL_READ =
        "UPDATE notifications SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE";

    private static final String SQL_DELETE_OLD =
        "DELETE FROM notifications WHERE user_id = ? AND created_at < DATE_SUB(NOW(), INTERVAL 30 DAY)";

    /**
     * Create a new notification.
     */
    public boolean createNotification(Notification notification) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setInt(1, notification.getUserId());
            stmt.setString(2, notification.getMessage());
            stmt.setString(3, notification.getType());
            stmt.setString(4, notification.getLink());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error creating notification: " + e.getMessage());
        }
        return false;
    }

    /**
     * Create notifications for multiple users (e.g., when expense is added).
     */
    public void notifyUsers(List<Integer> userIds, String message, String type, String link) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            for (int userId : userIds) {
                stmt.setInt(1, userId);
                stmt.setString(2, message);
                stmt.setString(3, type);
                stmt.setString(4, link);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error notifying users: " + e.getMessage());
        }
    }

    /**
     * Get notifications for a user.
     */
    public List<Notification> getUserNotifications(int userId, int limit) {
        List<Notification> notifications = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_USER_NOTIFICATIONS)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error getting notifications: " + e.getMessage());
        }
        return notifications;
    }

    /**
     * Get unread notification count.
     */
    public int getUnreadCount(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_UNREAD_COUNT)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error getting unread count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Mark a single notification as read.
     */
    public boolean markAsRead(int notificationId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_MARK_READ)) {
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error marking read: " + e.getMessage());
        }
        return false;
    }

    /**
     * Mark all notifications as read for a user.
     */
    public boolean markAllAsRead(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_MARK_ALL_READ)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error marking all read: " + e.getMessage());
        }
        return false;
    }

    /**
     * Delete notifications older than 30 days.
     */
    public void cleanOldNotifications(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_OLD)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[NotificationDAO] Error cleaning notifications: " + e.getMessage());
        }
    }

    private Notification mapResultSet(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getInt("id"));
        n.setUserId(rs.getInt("user_id"));
        n.setMessage(rs.getString("message"));
        n.setType(rs.getString("type"));
        n.setLink(rs.getString("link"));
        n.setRead(rs.getBoolean("is_read"));
        n.setCreatedAt(rs.getTimestamp("created_at"));
        return n;
    }
}
