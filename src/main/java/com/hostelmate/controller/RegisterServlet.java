package com.hostelmate.controller;

import com.hostelmate.dao.UserDAO;
import com.hostelmate.dao.RoomDAO;
import com.hostelmate.model.User;
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
 * RegisterServlet — Handles user registration.
 * 
 * GET  /register → Show registration form
 * POST /register → Process registration
 * 
 * @author HostelMate Team
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // If already logged in, redirect to dashboard
        if (SessionUtil.isLoggedIn(request)) {
            User user = SessionUtil.getLoggedInUser(request);
            if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/student/dashboard");
            }
            return;
        }

        // Load rooms for the dropdown
        request.setAttribute("rooms", roomDAO.getAllRooms());
        request.getRequestDispatcher("/jsp/auth/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ============================================================
        // 1. Extract form parameters
        // ============================================================
        String fullName        = ValidationUtil.clean(request.getParameter("fullName"));
        String email           = ValidationUtil.clean(request.getParameter("email"));
        String phone           = ValidationUtil.clean(request.getParameter("phone"));
        String password        = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        int    roomId          = ValidationUtil.parseIntSafe(request.getParameter("roomId"), 0);

        // ============================================================
        // 2. Server-side validation
        // ============================================================
        StringBuilder errors = new StringBuilder();

        if (!ValidationUtil.isValidName(fullName)) {
            errors.append("Please enter a valid full name. ");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            errors.append("Please enter a valid email address. ");
        }
        if (!ValidationUtil.isValidPhone(phone) && ValidationUtil.isNotEmpty(phone)) {
            errors.append("Please enter a valid 10-digit phone number. ");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            errors.append("Password must be at least 6 characters. ");
        }
        if (!ValidationUtil.passwordsMatch(password, confirmPassword)) {
            errors.append("Passwords do not match. ");
        }

        // Check if email already exists
        if (errors.length() == 0 && userDAO.emailExists(email)) {
            errors.append("An account with this email already exists. ");
        }

        // ============================================================
        // 3. Handle validation errors
        // ============================================================
        if (errors.length() > 0) {
            request.setAttribute("error", errors.toString().trim());
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("rooms", roomDAO.getAllRooms());
            request.getRequestDispatcher("/jsp/auth/register.jsp").forward(request, response);
            return;
        }

        // ============================================================
        // 4. Create the user
        // ============================================================
        User newUser = new User();
        newUser.setFullName(fullName);
        newUser.setEmail(email.toLowerCase());
        newUser.setPasswordHash(PasswordUtil.hashPassword(password));
        newUser.setPhone(phone);
        newUser.setRole("STUDENT");
        newUser.setRoomId(roomId);
        newUser.setProfilePic("default-avatar.png");
        newUser.setActive(true);

        int userId = userDAO.registerUser(newUser);

        if (userId > 0) {
            // Registration successful
            SessionUtil.setFlashMessage(request, "success",
                "Registration successful! Please log in.");
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            // Registration failed
            request.setAttribute("error", "Registration failed. Please try again.");
            request.setAttribute("fullName", fullName);
            request.setAttribute("email", email);
            request.setAttribute("phone", phone);
            request.setAttribute("rooms", roomDAO.getAllRooms());
            request.getRequestDispatcher("/jsp/auth/register.jsp").forward(request, response);
        }
    }
}
