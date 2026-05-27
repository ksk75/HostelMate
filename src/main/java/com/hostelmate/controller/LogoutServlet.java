package com.hostelmate.controller;

import com.hostelmate.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * LogoutServlet — Handles user logout.
 * 
 * GET /logout → Invalidate session and redirect to login
 * 
 * @author HostelMate Team
 */
@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Invalidate the session
        SessionUtil.invalidateSession(request);

        // Set a flash message and redirect to login
        SessionUtil.setFlashMessage(request, "success", "You have been logged out successfully.");
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
