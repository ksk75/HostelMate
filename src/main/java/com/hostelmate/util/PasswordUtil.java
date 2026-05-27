package com.hostelmate.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * PasswordUtil — BCrypt Password Hashing Utility
 * 
 * Provides methods to hash passwords and verify them using BCrypt.
 * BCrypt automatically handles salting, making it secure against
 * rainbow table attacks.
 * 
 * Requires: jbcrypt-0.4.jar in WEB-INF/lib/
 * 
 * @author HostelMate Team
 */
public class PasswordUtil {

    // BCrypt work factor (log2 rounds). 
    // 10 = 2^10 = 1024 iterations. Good balance of security and speed.
    private static final int BCRYPT_ROUNDS = 10;

    /**
     * Hash a plaintext password using BCrypt.
     * 
     * @param plainPassword the raw password from the user
     * @return BCrypt hashed string (includes salt)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verify a plaintext password against a BCrypt hash.
     * 
     * @param plainPassword the raw password to verify
     * @param hashedPassword the stored BCrypt hash
     * @return true if the password matches the hash
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format
            System.err.println("[HostelMate] Invalid hash format: " + e.getMessage());
            return false;
        }
    }
}
