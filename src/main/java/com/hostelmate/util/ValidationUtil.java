package com.hostelmate.util;

import java.util.regex.Pattern;

/**
 * ValidationUtil — Input Validation & Sanitization Utility
 * 
 * Provides server-side validation for all user inputs.
 * Helps prevent XSS, SQL injection, and invalid data.
 * 
 * @author HostelMate Team
 */
public class ValidationUtil {

    // Regular expression patterns
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[6-9]\\d{9}$");  // Indian mobile numbers

    private static final Pattern NAME_PATTERN = 
        Pattern.compile("^[A-Za-z\\s.'-]{2,100}$");

    private static final Pattern AMOUNT_PATTERN = 
        Pattern.compile("^\\d+(\\.\\d{1,2})?$");

    /**
     * Check if a string is null or empty (after trimming).
     * 
     * @param value the string to check
     * @return true if null or empty
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Check if a string is NOT null or empty.
     * 
     * @param value the string to check
     * @return true if not empty
     */
    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /**
     * Validate an email address format.
     * 
     * @param email the email to validate
     * @return true if valid email format
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate an Indian mobile phone number (10 digits, starts with 6-9).
     * 
     * @param phone the phone number to validate
     * @return true if valid phone number
     */
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return true;  // Phone is optional
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validate a person's name.
     * 
     * @param name the name to validate
     * @return true if valid name
     */
    public static boolean isValidName(String name) {
        if (isEmpty(name)) return false;
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Validate a password meets minimum requirements.
     * Requirements: at least 6 characters.
     * 
     * @param password the password to validate
     * @return true if valid password
     */
    public static boolean isValidPassword(String password) {
        if (isEmpty(password)) return false;
        return password.length() >= 6;
    }

    /**
     * Validate that two passwords match.
     * 
     * @param password        the password
     * @param confirmPassword the confirmation password
     * @return true if they match
     */
    public static boolean passwordsMatch(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) return false;
        return password.equals(confirmPassword);
    }

    /**
     * Validate an amount string (positive decimal number).
     * 
     * @param amount the amount string to validate
     * @return true if valid amount
     */
    public static boolean isValidAmount(String amount) {
        if (isEmpty(amount)) return false;
        if (!AMOUNT_PATTERN.matcher(amount.trim()).matches()) return false;
        try {
            double value = Double.parseDouble(amount.trim());
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parse a string to integer safely.
     * 
     * @param value        the string to parse
     * @param defaultValue value to return if parsing fails
     * @return parsed integer or default value
     */
    public static int parseIntSafe(String value, int defaultValue) {
        if (isEmpty(value)) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Parse a string to double safely.
     * 
     * @param value        the string to parse
     * @param defaultValue value to return if parsing fails
     * @return parsed double or default value
     */
    public static double parseDoubleSafe(String value, double defaultValue) {
        if (isEmpty(value)) return defaultValue;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Sanitize a string to prevent XSS.
     * Escapes HTML special characters.
     * 
     * @param input the raw input string
     * @return sanitized string safe for HTML output
     */
    public static String sanitizeHTML(String input) {
        if (input == null) return "";
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;");
    }

    /**
     * Trim and clean a string value.
     * Returns empty string if null.
     * 
     * @param value the string to clean
     * @return cleaned string
     */
    public static String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
