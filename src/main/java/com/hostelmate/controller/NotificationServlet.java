package com.hostelmate.controller;

import com.hostelmate.dao.NotificationDAO;
import com.hostelmate.model.Notification;
import com.hostelmate.util.SessionUtil;
import com.hostelmate.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * NotificationServlet — Handles notification operations.
 * 
 * GET  /student/notifications            → Get notifications (JSON for AJAX)
 * POST /student/notifications?action=read    → Mark one as read
 * POST /student/notifications?action=readAll → Mark all as read
 * 
 * @author HostelMate Team
 */
@WebServlet(name = "NotificationServlet", urlPatterns = {"/student/notifications"})
public class NotificationServlet extends HttpServlet {

    private final NotificationDAO notifDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = SessionUtil.getLoggedInUserId(request);
        int limit  = ValidationUtil.parseIntSafe(request.getParameter("limit"), 20);

        List<Notification> notifications = notifDAO.getUserNotifications(userId, limit);
        int unreadCount = notifDAO.getUnreadCount(userId);

        // Return JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        StringBuilder json = new StringBuilder();
        json.append("{\"unreadCount\":").append(unreadCount).append(",\"notifications\":[");

        for (int i = 0; i < notifications.size(); i++) {
            Notification n = notifications.get(i);
            if (i > 0) json.append(",");
            json.append("{");
            json.append("\"id\":").append(n.getId()).append(",");
            json.append("\"message\":\"").append(escapeJson(n.getMessage())).append("\",");
            json.append("\"type\":\"").append(n.getType()).append("\",");
            json.append("\"iconClass\":\"").append(n.getIconClass()).append("\",");
            json.append("\"timeAgo\":\"").append(n.getTimeAgo()).append("\",");
            json.append("\"read\":").append(n.isRead()).append(",");
            json.append("\"link\":\"").append(n.getLink() != null ? escapeJson(n.getLink()) : "").append("\"");
            json.append("}");
        }

        json.append("]}");
        out.print(json.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = SessionUtil.getLoggedInUserId(request);
        String action = request.getParameter("action");

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if ("readAll".equals(action)) {
            notifDAO.markAllAsRead(userId);
            out.print("{\"success\":true}");
        } else if ("read".equals(action)) {
            int notifId = ValidationUtil.parseIntSafe(request.getParameter("id"), 0);
            notifDAO.markAsRead(notifId);
            out.print("{\"success\":true}");
        } else {
            out.print("{\"success\":false}");
        }
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n");
    }
}
