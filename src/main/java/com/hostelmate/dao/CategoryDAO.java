package com.hostelmate.dao;

import com.hostelmate.model.Category;
import com.hostelmate.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CategoryDAO — Data Access Object for expense categories.
 * 
 * Categories are seeded in schema.sql and are read-only for users.
 * Admin can add new categories.
 * 
 * @author HostelMate Team
 */
public class CategoryDAO {

    private static final String SQL_GET_ALL =
        "SELECT * FROM categories ORDER BY name";

    private static final String SQL_FIND_BY_ID =
        "SELECT * FROM categories WHERE id = ?";

    private static final String SQL_INSERT =
        "INSERT INTO categories (name, icon, description) VALUES (?, ?, ?)";

    /**
     * Get all expense categories.
     * 
     * @return list of all categories
     */
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_ALL)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Category cat = new Category();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("name"));
                cat.setIcon(rs.getString("icon"));
                cat.setDescription(rs.getString("description"));
                cat.setCreatedAt(rs.getTimestamp("created_at"));
                categories.add(cat);
            }
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Error getting categories: " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }

    /**
     * Find a category by ID.
     * 
     * @param id the category ID
     * @return Category object, or null
     */
    public Category findById(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Category cat = new Category();
                cat.setId(rs.getInt("id"));
                cat.setName(rs.getString("name"));
                cat.setIcon(rs.getString("icon"));
                cat.setDescription(rs.getString("description"));
                cat.setCreatedAt(rs.getTimestamp("created_at"));
                return cat;
            }
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Error finding category: " + e.getMessage());
        }
        return null;
    }

    /**
     * Add a new category (admin feature).
     * 
     * @param category the category to add
     * @return true if added
     */
    public boolean addCategory(Category category) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setString(1, category.getName());
            stmt.setString(2, category.getIcon());
            stmt.setString(3, category.getDescription());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Error adding category: " + e.getMessage());
        }
        return false;
    }
}
