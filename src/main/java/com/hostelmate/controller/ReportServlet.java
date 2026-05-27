package com.hostelmate.controller;

import com.hostelmate.dao.ExpenseDAO;
import com.hostelmate.util.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ReportServlet — Generates report views.
 * 
 * GET /admin/reports → Show admin reports page
 * GET /student/reports → Show student reports page
 * 
 * @author HostelMate Team
 */
@WebServlet(name = "ReportServlet", urlPatterns = {"/admin/reports", "/student/reports"})
public class ReportServlet extends HttpServlet {

    private final ExpenseDAO expenseDAO = new ExpenseDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/admin/reports".equals(path)) {
            // Admin reports — all expenses
            request.setAttribute("expenses", expenseDAO.getAllExpenses());
            request.getRequestDispatcher("/jsp/admin/reports.jsp").forward(request, response);
        } else {
            // Student reports
            int userId = SessionUtil.getLoggedInUserId(request);
            request.setAttribute("expenses", expenseDAO.getUserExpenses(userId));

            // Category breakdown
            request.setAttribute("categoryBreakdown", expenseDAO.getCategoryBreakdown(userId));
            request.setAttribute("monthlyTrend", expenseDAO.getMonthlyTrend(userId));

            request.getRequestDispatcher("/jsp/student/reports.jsp").forward(request, response);
        }
    }
}
