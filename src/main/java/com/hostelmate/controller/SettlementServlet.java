package com.hostelmate.controller;

import com.hostelmate.dao.*;
import com.hostelmate.model.*;
import com.hostelmate.util.SessionUtil;
import com.hostelmate.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * SettlementServlet — Handles balance settlements between users.
 * 
 * GET  /student/settlements → List settlements and balances
 * POST /student/settlements?action=create   → Create settlement request
 * POST /student/settlements?action=complete → Mark settlement as complete
 * POST /student/settlements?action=cancel   → Cancel a settlement
 * POST /student/settlements?action=markPaid → Mark an expense share as paid
 * 
 * @author HostelMate Team
 */
@WebServlet(name = "SettlementServlet", urlPatterns = {"/student/settlements"})
public class SettlementServlet extends HttpServlet {

    private final SettlementDAO   settlementDAO = new SettlementDAO();
    private final ExpenseShareDAO shareDAO      = new ExpenseShareDAO();
    private final NotificationDAO notifDAO      = new NotificationDAO();
    private final UserDAO         userDAO       = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = SessionUtil.getLoggedInUser(request);
        int userId = user.getId();

        // Load balances
        List<Object[]> balances = shareDAO.getNetBalances(userId);
        request.setAttribute("balances", balances);

        // Load pending shares (what I owe others)
        request.setAttribute("pendingShares", shareDAO.getPendingShares(userId));

        // Load settlements
        request.setAttribute("settlements", settlementDAO.getUserSettlements(userId));
        request.setAttribute("pendingSettlements", settlementDAO.getPendingSettlements(userId));

        // Load all students for settlement creation
        request.setAttribute("students", userDAO.getAllStudents());

        request.getRequestDispatcher("/jsp/student/settlements.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user   = SessionUtil.getLoggedInUser(request);
        String action = request.getParameter("action");

        switch (action != null ? action : "") {
            case "create":
                handleCreate(request, response, user);
                break;
            case "complete":
                handleComplete(request, response, user);
                break;
            case "cancel":
                handleCancel(request, response, user);
                break;
            case "markPaid":
                handleMarkPaid(request, response, user);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/student/settlements");
        }
    }

    private void handleCreate(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        int toUserId = ValidationUtil.parseIntSafe(request.getParameter("toUserId"), 0);
        double amount = ValidationUtil.parseDoubleSafe(request.getParameter("amount"), 0);
        String notes = ValidationUtil.clean(request.getParameter("notes"));

        if (toUserId <= 0 || amount <= 0) {
            SessionUtil.setFlashMessage(request, "error", "Invalid settlement details.");
        } else {
            Settlement settlement = new Settlement(user.getId(), toUserId, amount, notes);
            int id = settlementDAO.createSettlement(settlement);
            if (id > 0) {
                // Notify the recipient
                User toUser = userDAO.findById(toUserId);
                notifDAO.createNotification(new Notification(toUserId,
                    user.getFullName() + " sent you ₹" + String.format("%,.2f", amount) + " settlement request",
                    "SETTLEMENT_REQUEST", "/student/settlements"));
                SessionUtil.setFlashMessage(request, "success", "Settlement request sent!");
            } else {
                SessionUtil.setFlashMessage(request, "error", "Failed to create settlement.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/student/settlements");
    }

    private void handleComplete(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        int settlementId = ValidationUtil.parseIntSafe(request.getParameter("settlementId"), 0);
        Settlement s = settlementDAO.findById(settlementId);

        if (s == null || (s.getFromUserId() != user.getId() && s.getToUserId() != user.getId())) {
            SessionUtil.setFlashMessage(request, "error", "Settlement not found.");
        } else if (settlementDAO.completeSettlement(settlementId)) {
            // Notify the other party
            int otherId = (s.getFromUserId() == user.getId()) ? s.getToUserId() : s.getFromUserId();
            notifDAO.createNotification(new Notification(otherId,
                user.getFullName() + " marked settlement of ₹" + String.format("%,.2f", s.getAmount()) + " as completed",
                "PAYMENT_RECEIVED", "/student/settlements"));
            SessionUtil.setFlashMessage(request, "success", "Settlement marked as completed!");
        } else {
            SessionUtil.setFlashMessage(request, "error", "Failed to complete settlement.");
        }
        response.sendRedirect(request.getContextPath() + "/student/settlements");
    }

    private void handleCancel(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        int settlementId = ValidationUtil.parseIntSafe(request.getParameter("settlementId"), 0);
        if (settlementDAO.cancelSettlement(settlementId)) {
            SessionUtil.setFlashMessage(request, "success", "Settlement cancelled.");
        } else {
            SessionUtil.setFlashMessage(request, "error", "Failed to cancel settlement.");
        }
        response.sendRedirect(request.getContextPath() + "/student/settlements");
    }

    private void handleMarkPaid(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        int shareId = ValidationUtil.parseIntSafe(request.getParameter("shareId"), 0);
        if (shareDAO.markAsPaid(shareId)) {
            SessionUtil.setFlashMessage(request, "success", "Payment marked as paid!");
        } else {
            SessionUtil.setFlashMessage(request, "error", "Failed to mark payment.");
        }
        response.sendRedirect(request.getContextPath() + "/student/settlements");
    }
}
