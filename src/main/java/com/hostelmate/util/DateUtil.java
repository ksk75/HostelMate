package com.hostelmate.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateUtil — Date Formatting Utility
 * 
 * Provides consistent date formatting across the application.
 * All dates are formatted in Indian standard format.
 * 
 * @author HostelMate Team
 */
public class DateUtil {

    // Standard date formats used in the application
    private static final String FORMAT_DATE      = "dd-MM-yyyy";
    private static final String FORMAT_DATETIME  = "dd-MM-yyyy HH:mm";
    private static final String FORMAT_SQL_DATE  = "yyyy-MM-dd";
    private static final String FORMAT_DISPLAY   = "dd MMM yyyy";
    private static final String FORMAT_MONTH     = "MMMM yyyy";

    /**
     * Format a Date object for display (e.g., "27 May 2026").
     * 
     * @param date the date to format
     * @return formatted date string
     */
    public static String formatForDisplay(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(FORMAT_DISPLAY).format(date);
    }

    /**
     * Format a Date object for SQL queries (e.g., "2026-05-27").
     * 
     * @param date the date to format
     * @return SQL-formatted date string
     */
    public static String formatForSQL(Date date) {
        if (date == null) return null;
        return new SimpleDateFormat(FORMAT_SQL_DATE).format(date);
    }

    /**
     * Format a Date as dd-MM-yyyy (e.g., "27-05-2026").
     * 
     * @param date the date to format
     * @return formatted date string
     */
    public static String formatDate(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(FORMAT_DATE).format(date);
    }

    /**
     * Format a Date with time (e.g., "27-05-2026 14:30").
     * 
     * @param date the date to format
     * @return formatted datetime string
     */
    public static String formatDateTime(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(FORMAT_DATETIME).format(date);
    }

    /**
     * Format a Date as month and year (e.g., "May 2026").
     * 
     * @param date the date to format
     * @return formatted month string
     */
    public static String formatMonth(Date date) {
        if (date == null) return "";
        return new SimpleDateFormat(FORMAT_MONTH).format(date);
    }

    /**
     * Parse a date string in dd-MM-yyyy format.
     * 
     * @param dateStr the date string to parse
     * @return parsed Date object, or null if invalid
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
            sdf.setLenient(false);
            return sdf.parse(dateStr.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Parse a date string in SQL format (yyyy-MM-dd).
     * 
     * @param dateStr the SQL date string to parse
     * @return parsed Date object, or null if invalid
     */
    public static Date parseSQLDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_SQL_DATE);
            sdf.setLenient(false);
            return sdf.parse(dateStr.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Convert a java.util.Date to java.sql.Date for database operations.
     * 
     * @param date the util Date
     * @return SQL Date
     */
    public static java.sql.Date toSQLDate(Date date) {
        if (date == null) return null;
        return new java.sql.Date(date.getTime());
    }

    /**
     * Get today's date as a SQL Date.
     * 
     * @return today's SQL date
     */
    public static java.sql.Date today() {
        return new java.sql.Date(System.currentTimeMillis());
    }
}
