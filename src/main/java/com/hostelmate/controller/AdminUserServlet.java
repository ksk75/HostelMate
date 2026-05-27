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
 * AdminUserServlet — Admin user management.
 * 
 * GET  /admin/users → List all users
 * POST /admin/users?action=add     → Add user
 * POST /admin/users?action=block   → Block/unblock user
 * POST /admin/users?action=delete  → Delete user
 * POST /admin/users?action=assignRoom → Assign room
 * 
 * @author HostelMate Team
 */
@WebServlet(name = "AdminUserServlet", urlPatterns = {"/admin/users"})
public class AdminUserServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("users", userDAO.getAllUsers());
        request.setAttribute("rooms", roomDAO.getAllRooms());
        request.getRequestDispatcher("/jsp/admin/users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        switch (action != null ? action : "") {
            case "add":
                handleAddUser(request, response);
                break;
            case "block":
                handleBlockUser(request, response);
                break;
            case "delete":
                handleDeleteUser(request, response);
                break;
            case "assignRoom":
                handleAssignRoom(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/users");
        }
    }

    private void handleAddUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fullName = ValidationUtil.clean(request.getParameter("fullName"));
        String email    = ValidationUtil.clean(request.getParameter("email"));
        String phone    = ValidationUtil.clean(request.getParameter("phone"));
        String password = request.getParameter("password");
        String role     = ValidationUtil.clean(request.getParameter("role"));
        int    roomId   = ValidationUtil.parseIntSafe(request.getParameter("roomId"), 0);

        if (ValidationUtil.isEmpty(fullName) || ValidationUtil.isEmpty(email) || ValidationUtil.isEmpty(password)) {
            SessionUtil.setFlashMessage(request, "error", "Name, email, and password are required.");
        } else if (userDAO.emailExists(email)) {
            SessionUtil.setFlashMessage(request, "error", "Email already exists.");
        } else {
            User newUser = new User();
            newUser.setFullName(fullName);
            newUser.setEmail(email.toLowerCase());
            newUser.setPasswordHash(PasswordUtil.hashPassword(password));
            newUser.setPhone(phone);
            newUser.setRole(ValidationUtil.isEmpty(role) ? "STUDENT" : role);
            newUser.setRoomId(roomId);
            newUser.setActive(true);

            if (userDAO.registerUser(newUser) > 0) {
                SessionUtil.setFlashMessage(request, "success", "User added successfully!");
            } else {
                SessionUtil.setFlashMessage(request, "error", "Failed to add user.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }

    private void handleBlockUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId  = ValidationUtil.parseIntSafe(request.getParameter("userId"), 0);
        boolean active = "true".equals(request.getParameter("active"));

        if (userDAO.toggleActive(userId, active)) {
            SessionUtil.setFlashMessage(request, "success",
                active ? "User unblocked successfully!" : "User blocked successfully!");
        } else {
            SessionUtil.setFlashMessage(request, "error", "Failed to update user status.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }

    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = ValidationUtil.parseIntSafe(request.getParameter("userId"), 0);

        if (userDAO.deleteUser(userId)) {
            SessionUtil.setFlashMessage(request, "success", "User deleted successfully!");
        } else {
            SessionUtil.setFlashMessage(request, "error", "Failed to delete user. Cannot delete admin accounts.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }

    private void handleAssignRoom(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = ValidationUtil.parseIntSafe(request.getParameter("userId"), 0);
        int roomId = ValidationUtil.parseIntSafe(request.getParameter("roomId"), 0);

        if (userDAO.updateRoom(userId, roomId)) {
            SessionUtil.setFlashMessage(request, "success", "Room assigned successfully!");
        } else {
            SessionUtil.setFlashMessage(request, "error", "Failed to assign room.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/users");
    }
}
