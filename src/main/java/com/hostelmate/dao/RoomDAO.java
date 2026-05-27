package com.hostelmate.dao;

import com.hostelmate.model.Room;
import com.hostelmate.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * RoomDAO — Data Access Object for hostel rooms.
 * 
 * @author HostelMate Team
 */
public class RoomDAO {

    private static final String SQL_GET_ALL =
        "SELECT r.*, (SELECT COUNT(*) FROM users u WHERE u.room_id = r.id AND u.is_active = TRUE) " +
        "AS occupant_count FROM rooms r ORDER BY r.room_number";

    private static final String SQL_FIND_BY_ID =
        "SELECT r.*, (SELECT COUNT(*) FROM users u WHERE u.room_id = r.id AND u.is_active = TRUE) " +
        "AS occupant_count FROM rooms r WHERE r.id = ?";

    private static final String SQL_INSERT =
        "INSERT INTO rooms (room_number, floor, capacity) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE =
        "UPDATE rooms SET room_number = ?, floor = ?, capacity = ?, " +
        "updated_at = CURRENT_TIMESTAMP WHERE id = ?";

    private static final String SQL_DELETE =
        "DELETE FROM rooms WHERE id = ?";

    private static final String SQL_COUNT =
        "SELECT COUNT(*) FROM rooms";

    private static final String SQL_ROOM_EXISTS =
        "SELECT COUNT(*) FROM rooms WHERE room_number = ?";

    /**
     * Get all rooms with occupant counts.
     */
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_ALL)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                rooms.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("[RoomDAO] Error getting rooms: " + e.getMessage());
        }
        return rooms;
    }

    /**
     * Find a room by ID.
     */
    public Room findById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("[RoomDAO] Error finding room: " + e.getMessage());
        }
        return null;
    }

    /**
     * Add a new room.
     */
    public boolean addRoom(Room room) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setString(1, room.getRoomNumber());
            stmt.setInt(2, room.getFloor());
            stmt.setInt(3, room.getCapacity());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[RoomDAO] Error adding room: " + e.getMessage());
        }
        return false;
    }

    /**
     * Update a room.
     */
    public boolean updateRoom(Room room) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setString(1, room.getRoomNumber());
            stmt.setInt(2, room.getFloor());
            stmt.setInt(3, room.getCapacity());
            stmt.setInt(4, room.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[RoomDAO] Error updating room: " + e.getMessage());
        }
        return false;
    }

    /**
     * Delete a room (only if no users assigned).
     */
    public boolean deleteRoom(int roomId) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, roomId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[RoomDAO] Error deleting room: " + e.getMessage());
        }
        return false;
    }

    /**
     * Get total room count.
     */
    public int countRooms() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_COUNT)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("[RoomDAO] Error counting rooms: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Check if a room number already exists.
     */
    public boolean roomExists(String roomNumber) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_ROOM_EXISTS)) {
            stmt.setString(1, roomNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[RoomDAO] Error checking room: " + e.getMessage());
        }
        return false;
    }

    private Room mapResultSet(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setFloor(rs.getInt("floor"));
        room.setCapacity(rs.getInt("capacity"));
        room.setCreatedAt(rs.getTimestamp("created_at"));
        room.setUpdatedAt(rs.getTimestamp("updated_at"));
        try {
            room.setOccupantCount(rs.getInt("occupant_count"));
        } catch (SQLException e) { /* column not present */ }
        return room;
    }
}
