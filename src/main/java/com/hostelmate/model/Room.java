package com.hostelmate.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Room — Model class for hostel rooms.
 * Maps to the 'rooms' table.
 * 
 * @author HostelMate Team
 */
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    private int       id;
    private String    roomNumber;
    private int       floor;
    private int       capacity;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Computed fields
    private int       occupantCount;  // Current number of residents

    // ============================================================
    // Constructors
    // ============================================================
    public Room() {}

    public Room(String roomNumber, int floor, int capacity) {
        this.roomNumber = roomNumber;
        this.floor      = floor;
        this.capacity   = capacity;
    }

    // ============================================================
    // Getters and Setters
    // ============================================================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public int getOccupantCount() { return occupantCount; }
    public void setOccupantCount(int occupantCount) { this.occupantCount = occupantCount; }

    /** Check if room has available beds */
    public boolean hasVacancy() {
        return occupantCount < capacity;
    }

    /** Get available beds count */
    public int getAvailableBeds() {
        return capacity - occupantCount;
    }

    @Override
    public String toString() {
        return "Room{id=" + id + ", number='" + roomNumber + "', floor=" + floor +
               ", capacity=" + capacity + ", occupants=" + occupantCount + "}";
    }
}
