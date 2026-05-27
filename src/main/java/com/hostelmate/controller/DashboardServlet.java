package com.hostelmate.controller;

import com.hostelmate.dao.*;
import com.hostelmate.model.*;
import com.hostelmate.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * DashboardServlet — Loads dashboard data for students and admins.
 * 
 * GET /student/dashboard → Student dashboard
 * GET /admin/dashboard   → Admin dashboard
 * 
 * @author HostelMate Team
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/student/dashboard", "/admin/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final ExpenseDAO      expenseDAO = new ExpenseDAO();
    private final ExpenseShareDAO shareDAO   = new ExpenseShareDAO();
    private final UserDAO         userDAO    = new UserDAO();
    private final RoomDAO         roomDAO    = new RoomDAO();
    private final NotificationDAO notifDAO   = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = SessionUtil.getLoggedInUser(request);
        String path = request.getServletPath();

        if ("/admin/dashboard".equals(path)) {
            loadAdminDashboard(request, user);
            request.getRequestDispatcher("/jsp/admin/dashboard.jsp").forward(request, response);
        } else {
            loadStudentDashboard(request, user);
            request.getRequestDispatcher("/jsp/student/dashboard.jsp").forward(request, response);
        }
    }

    private void loadStudentDashboard(HttpServletRequest request, User user) {
        int userId = user.getId();
        LocalDate now = LocalDate.now();

        // ============================================================
        // Summary cards
        // ============================================================
        double monthlyExpenses = expenseDAO.getMonthlyExpenses(userId, now.getMonthValue(), now.getYear());
        double pendingDues     = shareDAO.getPendingAmount(userId);
        double totalPaid       = shareDAO.getPaidAmount(userId);
        double totalExpenses   = expenseDAO.getTotalExpensesByUser(userId);

        request.setAttribute("monthlyExpenses", monthlyExpenses);
        request.setAttribute("pendingDues", pendingDues);
        request.setAttribute("totalPaid", totalPaid);
        request.setAttribute("totalExpenses", totalExpenses);

        // ============================================================
        // Recent transactions
        // ============================================================
        List<Expense> recentExpenses = expenseDAO.getRecentExpenses(userId, 5);
        request.setAttribute("recentExpenses", recentExpenses);

        // ============================================================
        // Chart data: Category breakdown
        // ============================================================
        List<Object[]> categoryBreakdown = expenseDAO.getCategoryBreakdown(userId);
        StringBuilder catLabels = new StringBuilder("[");
        StringBuilder catData   = new StringBuilder("[");
        for (int i = 0; i < categoryBreakdown.size(); i++) {
            Object[] row = categoryBreakdown.get(i);
            if (i > 0) { catLabels.append(","); catData.append(","); }
            catLabels.append("\"").append(row[0]).append("\"");
            catData.append(row[2]);
        }
        catLabels.append("]");
        catData.append("]");
        request.setAttribute("categoryLabels", catLabels.toString());
        request.setAttribute("categoryData", catData.toString());

        // ============================================================
        // Chart data: Monthly trend
        // ============================================================
        List<Object[]> monthlyTrend = expenseDAO.getMonthlyTrend(userId);
        String[] monthNames = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                               "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        StringBuilder trendLabels = new StringBuilder("[");
        StringBuilder trendData   = new StringBuilder("[");
        for (int i = 0; i < monthlyTrend.size(); i++) {
            Object[] row = monthlyTrend.get(i);
            if (i > 0) { trendLabels.append(","); trendData.append(","); }
            int month = (int) row[0];
            trendLabels.append("\"").append(monthNames[month]).append(" ").append(row[1]).append("\"");
            trendData.append(row[2]);
        }
        trendLabels.append("]");
        trendData.append("]");
        request.setAttribute("trendLabels", trendLabels.toString());
        request.setAttribute("trendData", trendData.toString());

        // ============================================================
        // Balance summary
        // ============================================================
        List<Object[]> balances = shareDAO.getNetBalances(userId);
        request.setAttribute("balances", balances);

        // ============================================================
        // Notifications
        // ============================================================
        request.setAttribute("notifications", notifDAO.getUserNotifications(userId, 10));
        request.setAttribute("unreadCount", notifDAO.getUnreadCount(userId));
    }

    private void loadAdminDashboard(HttpServletRequest request, User user) {
        // ============================================================
        // Admin summary cards
        // ============================================================
        int totalStudents       = userDAO.countActiveStudents();
        int totalRooms          = roomDAO.countRooms();
        double totalExpenses    = expenseDAO.getTotalAllExpenses();
        double monthlyCollection = expenseDAO.getMonthlyCollection();
        double pendingPayments  = shareDAO.getTotalPendingAll();

        request.setAttribute("totalStudents", totalStudents);
        request.setAttribute("totalRooms", totalRooms);
        request.setAttribute("totalExpenses", totalExpenses);
        request.setAttribute("monthlyCollection", monthlyCollection);
        request.setAttribute("pendingPayments", pendingPayments);

        // Recent expenses (all)
        request.setAttribute("recentExpenses", expenseDAO.getAllExpenses());

        // All users for management
        request.setAttribute("allUsers", userDAO.getAllUsers());

        // Notifications
        request.setAttribute("notifications", notifDAO.getUserNotifications(user.getId(), 10));
        request.setAttribute("unreadCount", notifDAO.getUnreadCount(user.getId()));
    }
}
