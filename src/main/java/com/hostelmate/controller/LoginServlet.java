package com.hostelmate.controller;

import com.hostelmate.dao.UserDAO;
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
 * LoginServlet — Handles user authentication.
 * 
 * GET  /login → Show login form
 * POST /login → Process login credentials
 * 
 * @author HostelMate Team
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

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

        request.getRequestDispatcher("/jsp/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ============================================================
        // 1. Extract credentials
        // ============================================================
        String email    = ValidationUtil.clean(request.getParameter("email"));
        String password = request.getParameter("password");

        // ============================================================
        // 2. Validate input
        // ============================================================
        if (ValidationUtil.isEmpty(email) || ValidationUtil.isEmpty(password)) {
            request.setAttribute("error", "Please enter both email and password.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/jsp/auth/login.jsp").forward(request, response);
            return;
        }

        // ============================================================
        // 3. Find user by email
        // ============================================================
        User user = userDAO.findByEmail(email.toLowerCase());

        if (user == null) {
            request.setAttribute("error", "Invalid email or password.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/jsp/auth/login.jsp").forward(request, response);
            return;
        }

        // ============================================================
        // 4. Verify password
        // ============================================================
        if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
            request.setAttribute("error", "Invalid email or password.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/jsp/auth/login.jsp").forward(request, response);
            return;
        }

        // ============================================================
        // 5. Check if account is active
        // ============================================================
        if (!user.isActive()) {
            request.setAttribute("error", "Your account has been blocked. Contact the administrator.");
            request.getRequestDispatcher("/jsp/auth/login.jsp").forward(request, response);
            return;
        }

        // ============================================================
        // 6. Create session and redirect
        // ============================================================
        SessionUtil.setLoggedInUser(request, user);

        // Redirect based on role
        if (user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/admin/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/student/dashboard");
        }
    }
}
