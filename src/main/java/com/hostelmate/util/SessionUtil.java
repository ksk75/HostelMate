package com.hostelmate.util;

import com.hostelmate.model.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * SessionUtil — HTTP Session Helper Utility
 * 
 * Provides convenience methods for session management:
 * - Get/set the logged-in user
 * - Check authentication status
 * - Check user roles
 * - Invalidate sessions
 * 
 * @author HostelMate Team
 */
public class SessionUtil {

    // Session attribute keys
    public static final String USER_KEY    = "loggedInUser";
    public static final String USER_ID_KEY = "userId";
    public static final String ROLE_KEY    = "userRole";

    /**
     * Store the logged-in user in the session.
     * 
     * @param request the HTTP request
     * @param user    the authenticated user
     */
    public static void setLoggedInUser(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(USER_KEY, user);
        session.setAttribute(USER_ID_KEY, user.getId());
        session.setAttribute(ROLE_KEY, user.getRole());
    }

    /**
     * Get the logged-in user from the session.
     * 
     * @param request the HTTP request
     * @return User object, or null if not logged in
     */
    public static User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute(USER_KEY);
    }

    /**
     * Get the logged-in user's ID.
     * 
     * @param request the HTTP request
     * @return user ID, or -1 if not logged in
     */
    public static int getLoggedInUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return -1;
        }
        Object id = session.getAttribute(USER_ID_KEY);
        return id != null ? (int) id : -1;
    }

    /**
     * Check if a user is currently logged in.
     * 
     * @param request the HTTP request
     * @return true if logged in
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        return getLoggedInUser(request) != null;
    }

    /**
     * Check if the logged-in user has ADMIN role.
     * 
     * @param request the HTTP request
     * @return true if user is admin
     */
    public static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        String role = (String) session.getAttribute(ROLE_KEY);
        return "ADMIN".equals(role);
    }

    /**
     * Check if the logged-in user has STUDENT role.
     * 
     * @param request the HTTP request
     * @return true if user is student
     */
    public static boolean isStudent(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        String role = (String) session.getAttribute(ROLE_KEY);
        return "STUDENT".equals(role);
    }

    /**
     * Invalidate the current session (logout).
     * 
     * @param request the HTTP request
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * Set a flash message in the session (for displaying after redirect).
     * 
     * @param request the HTTP request
     * @param type    message type: "success", "error", "warning", "info"
     * @param message the message text
     */
    public static void setFlashMessage(HttpServletRequest request, String type, String message) {
        HttpSession session = request.getSession(true);
        session.setAttribute("flashType", type);
        session.setAttribute("flashMessage", message);
    }

    /**
     * Get and clear a flash message from the session.
     * Returns null if no flash message exists.
     * 
     * @param request the HTTP request
     * @return array [type, message] or null
     */
    public static String[] getFlashMessage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;

        String type = (String) session.getAttribute("flashType");
        String message = (String) session.getAttribute("flashMessage");

        if (type != null && message != null) {
            // Clear after reading (flash behavior)
            session.removeAttribute("flashType");
            session.removeAttribute("flashMessage");
            return new String[]{type, message};
        }
        return null;
    }
}
