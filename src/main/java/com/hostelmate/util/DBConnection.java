package com.hostelmate.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection — Singleton JDBC Connection Utility
 * 
 * Provides database connections to MySQL using JDBC.
 * Uses a simple connection factory pattern suitable for
 * a Tomcat-based web application.
 * 
 * Configuration:
 *   - Update DB_URL, DB_USER, DB_PASSWORD to match your MySQL setup.
 *   - Ensure mysql-connector-j JAR is in WEB-INF/lib/
 * 
 * @author HostelMate Team
 */
public class DBConnection {

    // ============================================================
    // Database Configuration
    // ============================================================
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/hostelmate?useSSL=false&serverTimezone=Asia/Kolkata&allowPublicKeyRetrieval=true";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "";  // Change to your MySQL password
    private static final String DB_DRIVER   = "com.mysql.cj.jdbc.Driver";

    // Static block to load the JDBC driver once
    static {
        try {
            Class.forName(DB_DRIVER);
            System.out.println("[HostelMate] MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("[HostelMate] ERROR: MySQL JDBC Driver not found!");
            System.err.println("[HostelMate] Make sure mysql-connector-j-8.x.jar is in WEB-INF/lib/");
            throw new RuntimeException("Failed to load MySQL JDBC Driver", e);
        }
    }

    /**
     * Get a new database connection.
     * Each call returns a fresh connection — caller must close it.
     * 
     * Usage:
     *   try (Connection conn = DBConnection.getConnection()) {
     *       // use connection
     *   }
     * 
     * @return Connection object to the hostelmate database
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        return conn;
    }

    /**
     * Safely close a connection (null-safe).
     * 
     * @param conn the connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("[HostelMate] Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * Test the database connection.
     * Can be called from a servlet or main method to verify setup.
     * 
     * @return true if connection is successful
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("[HostelMate] Database connection test: SUCCESS");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[HostelMate] Database connection test: FAILED");
            System.err.println("[HostelMate] Error: " + e.getMessage());
        }
        return false;
    }
}
