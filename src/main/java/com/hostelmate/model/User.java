package com.hostelmate.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * User — Model class representing a hostel resident or admin.
 * 
 * Maps to the 'users' table in the database.
 * Implements Serializable for session storage.
 * 
 * @author HostelMate Team
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    // ============================================================
    // Fields (matching database columns)
    // ============================================================
    private int       id;
    private String    fullName;
    private String    email;
    private String    passwordHash;
    private String    phone;
    private String    role;          // "ADMIN" or "STUDENT"
    private int       roomId;
    private String    profilePic;
    private boolean   active;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Additional fields for display (joined from rooms table)
    private String    roomNumber;

    // ============================================================
    // Constructors
    // ============================================================

    /** Default constructor */
    public User() {
    }

    /** Constructor for registration */
    public User(String fullName, String email, String passwordHash, String phone, String role) {
        this.fullName     = fullName;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.phone        = phone;
        this.role         = role;
        this.active       = true;
        this.profilePic   = "default-avatar.png";
    }

    /** Full constructor */
    public User(int id, String fullName, String email, String passwordHash,
                String phone, String role, int roomId, String profilePic,
                boolean active, Timestamp createdAt, Timestamp updatedAt) {
        this.id           = id;
        this.fullName     = fullName;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.phone        = phone;
        this.role         = role;
        this.roomId       = roomId;
        this.profilePic   = profilePic;
        this.active       = active;
        this.createdAt    = createdAt;
        this.updatedAt    = updatedAt;
    }

    // ============================================================
    // Getters and Setters
    // ============================================================

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getProfilePic() { return profilePic; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    // ============================================================
    // Convenience Methods
    // ============================================================

    /** Check if this user is an admin */
    public boolean isAdmin() {
        return "ADMIN".equals(this.role);
    }

    /** Check if this user is a student */
    public boolean isStudent() {
        return "STUDENT".equals(this.role);
    }

    /** Get the user's first name (for display) */
    public String getFirstName() {
        if (fullName == null || fullName.isEmpty()) return "";
        String[] parts = fullName.split("\\s+");
        return parts[0];
    }

    /** Get initials for avatar display */
    public String getInitials() {
        if (fullName == null || fullName.isEmpty()) return "?";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length >= 2) {
            return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
        }
        return ("" + parts[0].charAt(0)).toUpperCase();
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + fullName + "', email='" + email + 
               "', role='" + role + "', roomId=" + roomId + "}";
    }
}
