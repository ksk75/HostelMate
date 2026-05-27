package com.hostelmate.dao;

import com.hostelmate.model.User;
import com.hostelmate.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO — Data Access Object for User operations.
 * 
 * Handles all database queries related to users:
 * - Registration (insert)
 * - Login (find by email)
 * - Profile updates
 * - Password changes
 * - Admin user management
 * 
 * All queries use PreparedStatement for SQL injection prevention.
 * 
 * @author HostelMate Team
 */
public class UserDAO {

    // ============================================================
    // SQL Queries (constants for maintainability)
    // ============================================================

    private static final String SQL_INSERT_USER =
        "INSERT INTO users (full_name, email, password_hash, phone, role, room_id, profile_pic, is_active) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_EMAIL =
        "SELECT u.*, r.room_number FROM users u " +
        "LEFT JOIN rooms r ON u.room_id = r.id " +
        "WHERE u.email = ?";

    private static final String SQL_FIND_BY_ID =
        "SELECT u.*, r.room_number FROM users u " +
        "LEFT JOIN rooms r ON u.room_id = r.id " +
        "WHERE u.id = ?";

    private static final String SQL_UPDATE_PROFILE =
        "UPDATE users SET full_name = ?, phone = ?, room_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String SQL_UPDATE_PASSWORD =
        "UPDATE users SET password_hash = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String SQL_UPDATE_PROFILE_PIC =
        "UPDATE users SET profile_pic = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String SQL_GET_ALL_STUDENTS =
        "SELECT u.*, r.room_number FROM users u " +
        "LEFT JOIN rooms r ON u.room_id = r.id " +
        "WHERE u.role = 'STUDENT' ORDER BY u.full_name";

    private static final String SQL_GET_ALL_USERS =
        "SELECT u.*, r.room_number FROM users u " +
        "LEFT JOIN rooms r ON u.room_id = r.id " +
        "ORDER BY u.role DESC, u.full_name";

    private static final String SQL_TOGGLE_ACTIVE =
        "UPDATE users SET is_active = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String SQL_DELETE_USER =
        "DELETE FROM users WHERE id = ? AND role <> 'ADMIN'";

    private static final String SQL_COUNT_STUDENTS =
        "SELECT COUNT(*) FROM users WHERE role = 'STUDENT' AND is_active = TRUE";

    private static final String SQL_GET_ROOMMATES =
        "SELECT u.*, r.room_number FROM users u " +
        "LEFT JOIN rooms r ON u.room_id = r.id " +
        "WHERE u.room_id = ? AND u.is_active = TRUE ORDER BY u.full_name";

    private static final String SQL_EMAIL_EXISTS =
        "SELECT COUNT(*) FROM users WHERE email = ?";

    private static final String SQL_UPDATE_ROOM =
        "UPDATE users SET room_id = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";

    // ============================================================
    // CRUD Operations
    // ============================================================

    /**
     * Register a new user.
     * 
     * @param user the user to register
     * @return the generated user ID, or -1 on failure
     */
    public int registerUser(User user) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setString(4, user.getPhone());
            stmt.setString(5, user.getRole());
            if (user.getRoomId() > 0) {
                stmt.setInt(6, user.getRoomId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setString(7, user.getProfilePic() != null ? user.getProfilePic() : "default-avatar.png");
            stmt.setBoolean(8, true);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error registering user: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Find a user by email address (for login).
     * 
     * @param email the email to search
     * @return User object, or null if not found
     */
    public User findByEmail(String email) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_EMAIL)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error finding user by email: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Find a user by ID.
     * 
     * @param id the user ID
     * @return User object, or null if not found
     */
    public User findById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error finding user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update user profile information.
     * 
     * @param user the user with updated fields
     * @return true if updated successfully
     */
    public boolean updateProfile(User user) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_PROFILE)) {

            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getPhone());
            if (user.getRoomId() > 0) {
                stmt.setInt(3, user.getRoomId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setInt(4, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error updating profile: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Change a user's password.
     * 
     * @param userId       the user's ID
     * @param newPasswordHash the new BCrypt-hashed password
     * @return true if updated successfully
     */
    public boolean changePassword(int userId, String newPasswordHash) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_PASSWORD)) {

            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error changing password: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Update profile picture filename.
     * 
     * @param userId     the user's ID
     * @param profilePic the filename of the profile picture
     * @return true if updated
     */
    public boolean updateProfilePic(int userId, String profilePic) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_PROFILE_PIC)) {

            stmt.setString(1, profilePic);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error updating profile pic: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get all students (active and inactive).
     * 
     * @return list of student users
     */
    public List<User> getAllStudents() {
        List<User> students = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_ALL_STUDENTS)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                students.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error getting all students: " + e.getMessage());
            e.printStackTrace();
        }
        return students;
    }

    /**
     * Get all users (for admin management).
     * 
     * @return list of all users
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_ALL_USERS)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error getting all users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Get all roommates for a given room.
     * 
     * @param roomId the room ID
     * @return list of users in that room
     */
    public List<User> getRoommates(int roomId) {
        List<User> roommates = new ArrayList<>();
        if (roomId <= 0) return roommates;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_ROOMMATES)) {

            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                roommates.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error getting roommates: " + e.getMessage());
            e.printStackTrace();
        }
        return roommates;
    }

    /**
     * Toggle a user's active status (block/unblock).
     * 
     * @param userId the user's ID
     * @param active the new active status
     * @return true if updated
     */
    public boolean toggleActive(int userId, boolean active) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_TOGGLE_ACTIVE)) {

            stmt.setBoolean(1, active);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error toggling user active status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete a user (admin only, cannot delete other admins).
     * 
     * @param userId the user's ID
     * @return true if deleted
     */
    public boolean deleteUser(int userId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_USER)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Count active students.
     * 
     * @return number of active students
     */
    public int countActiveStudents() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT_STUDENTS)) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error counting students: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Check if an email already exists in the database.
     * 
     * @param email the email to check
     * @return true if email already exists
     */
    public boolean emailExists(String email) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_EMAIL_EXISTS)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error checking email: " + e.getMessage());
        }
        return false;
    }

    /**
     * Update user's room assignment.
     * 
     * @param userId the user's ID
     * @param roomId the new room ID (0 to unassign)
     * @return true if updated
     */
    public boolean updateRoom(int userId, int roomId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_ROOM)) {

            if (roomId > 0) {
                stmt.setInt(1, roomId);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] Error updating room: " + e.getMessage());
        }
        return false;
    }

    // ============================================================
    // Helper Methods
    // ============================================================

    /**
     * Map a ResultSet row to a User object.
     * 
     * @param rs the ResultSet positioned at a row
     * @return populated User object
     * @throws SQLException on database error
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));

        int roomId = rs.getInt("room_id");
        user.setRoomId(rs.wasNull() ? 0 : roomId);

        user.setProfilePic(rs.getString("profile_pic"));
        user.setActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));

        // Try to get room_number (from JOIN)
        try {
            user.setRoomNumber(rs.getString("room_number"));
        } catch (SQLException e) {
            // Column not in result set — that's ok
        }

        return user;
    }
}
