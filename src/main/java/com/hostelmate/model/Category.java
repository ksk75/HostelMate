package com.hostelmate.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Category — Model class for expense categories.
 * Maps to the 'categories' table.
 * 
 * @author HostelMate Team
 */
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    private int       id;
    private String    name;
    private String    icon;        // Bootstrap icon class
    private String    description;
    private Timestamp createdAt;

    // ============================================================
    // Constructors
    // ============================================================
    public Category() {}

    public Category(int id, String name, String icon, String description) {
        this.id          = id;
        this.name        = name;
        this.icon        = icon;
        this.description = description;
    }

    // ============================================================
    // Getters and Setters
    // ============================================================
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Category{id=" + id + ", name='" + name + "'}";
    }
}
