package com.hostelmate.controller;

import com.hostelmate.dao.*;
import com.hostelmate.model.*;
import com.hostelmate.util.PasswordUtil;
import com.hostelmate.util.SessionUtil;
import com.hostelmate.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ProfileServlet — Handles user profile management.
 * 
 * GET  /student/profile → View profile
 * POST /student/profile?action=update   → Update profile info
 * POST /student/profile?action=password → Change password
 * 
 * @author HostelMate Team
 */
@WebServlet(name = "ProfileServlet", urlPatterns = {"/student/profile"})
public class ProfileServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = SessionUtil.getLoggedInUser(request);
        // Refresh user data from DB
        User freshUser = userDAO.findById(user.getId());
        if (freshUser != null) {
            request.setAttribute("profileUser", freshUser);
        } else {
            request.setAttribute("profileUser", user);
        }

        request.setAttribute("rooms", roomDAO.getAllRooms());
        request.getRequestDispatcher("/jsp/profile/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user   = SessionUtil.getLoggedInUser(request);
        String action = request.getParameter("action");

        if ("update".equals(action)) {
            handleUpdateProfile(request, response, user);
        } else if ("password".equals(action)) {
            handleChangePassword(request, response, user);
        } else {
            response.sendRedirect(request.getContextPath() + "/student/profile");
        }
    }

    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {

        String fullName = ValidationUtil.clean(request.getParameter("fullName"));
        String phone    = ValidationUtil.clean(request.getParameter("phone"));
        int    roomId   = ValidationUtil.parseIntSafe(request.getParameter("roomId"), 0);

        if (!ValidationUtil.isValidName(fullName)) {
            SessionUtil.setFlashMessage(request, "error", "Please enter a valid name.");
            response.sendRedirect(request.getContextPath() + "/student/profile");
            return;
        }

        user.setFullName(fullName);
        user.setPhone(phone);
        user.setRoomId(roomId);

        if (userDAO.updateProfile(user)) {
            // Refresh session user
            User updatedUser = userDAO.findById(user.getId());
            SessionUtil.setLoggedInUser(request, updatedUser);
            SessionUtil.setFlashMessage(request, "success", "Profile updated successfully!");
        } else {
            SessionUtil.setFlashMessage(request, "error", "Failed to update profile.");
        }

        response.sendRedirect(request.getContextPath() + "/student/profile");
    }

    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {

        String currentPassword = request.getParameter("currentPassword");
        String newPassword     = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Verify current password
        if (!PasswordUtil.verifyPassword(currentPassword, user.getPasswordHash())) {
            SessionUtil.setFlashMessage(request, "error", "Current password is incorrect.");
            response.sendRedirect(request.getContextPath() + "/student/profile");
            return;
        }

        // Validate new password
        if (!ValidationUtil.isValidPassword(newPassword)) {
            SessionUtil.setFlashMessage(request, "error", "New password must be at least 6 characters.");
            response.sendRedirect(request.getContextPath() + "/student/profile");
            return;
        }

        if (!ValidationUtil.passwordsMatch(newPassword, confirmPassword)) {
            SessionUtil.setFlashMessage(request, "error", "New passwords do not match.");
            response.sendRedirect(request.getContextPath() + "/student/profile");
            return;
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        if (userDAO.changePassword(user.getId(), newHash)) {
            // Update session
            user.setPasswordHash(newHash);
            SessionUtil.setLoggedInUser(request, user);
            SessionUtil.setFlashMessage(request, "success", "Password changed successfully!");
        } else {
            SessionUtil.setFlashMessage(request, "error", "Failed to change password.");
        }

        response.sendRedirect(request.getContextPath() + "/student/profile");
    }
}
