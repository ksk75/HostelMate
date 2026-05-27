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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * ExpenseServlet — Handles all expense CRUD operations.
 * 
 * URL patterns:
 *   GET  /student/expenses         → List expenses
 *   GET  /student/expenses?action=add    → Show add form
 *   POST /student/expenses?action=add    → Add expense
 *   POST /student/expenses?action=edit   → Edit expense
 *   POST /student/expenses?action=delete → Delete expense
 *   GET  /student/expenses?action=view&id=X → View expense details (AJAX JSON)
 * 
 * @author HostelMate Team
 */
@WebServlet(name = "ExpenseServlet", urlPatterns = {"/student/expenses"})
public class ExpenseServlet extends HttpServlet {

    private final ExpenseDAO      expenseDAO      = new ExpenseDAO();
    private final CategoryDAO     categoryDAO     = new CategoryDAO();
    private final UserDAO         userDAO         = new UserDAO();
    private final ExpenseShareDAO shareDAO        = new ExpenseShareDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user   = SessionUtil.getLoggedInUser(request);
        String action = request.getParameter("action");

        if ("view".equals(action)) {
            // Return expense details as JSON (for modal)
            handleViewExpense(request, response);
            return;
        }

        // ============================================================
        // Load data for expense list page
        // ============================================================

        // Get filter parameters
        String keyword    = request.getParameter("search");
        String categoryId = request.getParameter("category");
        String startDate  = request.getParameter("startDate");
        String endDate    = request.getParameter("endDate");

        List<Expense> expenses;

        if (ValidationUtil.isNotEmpty(keyword)) {
            // Search mode
            expenses = expenseDAO.searchExpenses(user.getId(), keyword);
        } else if (ValidationUtil.isNotEmpty(categoryId) || 
                   ValidationUtil.isNotEmpty(startDate) || 
                   ValidationUtil.isNotEmpty(endDate)) {
            // Filter mode
            expenses = expenseDAO.getFilteredExpenses(
                user.getId(),
                ValidationUtil.parseIntSafe(categoryId, 0),
                ValidationUtil.isNotEmpty(startDate) ? java.sql.Date.valueOf(startDate) : null,
                ValidationUtil.isNotEmpty(endDate)   ? java.sql.Date.valueOf(endDate)   : null
            );
        } else {
            // Default: all user expenses
            expenses = expenseDAO.getUserExpenses(user.getId());
        }

        request.setAttribute("expenses", expenses);
        request.setAttribute("categories", categoryDAO.getAllCategories());

        // Get roommates + all students for the share dropdown
        List<User> students = userDAO.getAllStudents();
        request.setAttribute("students", students);

        // Get roommates for quick access
        if (user.getRoomId() > 0) {
            request.setAttribute("roommates", userDAO.getRoommates(user.getRoomId()));
        }

        request.getRequestDispatcher("/jsp/student/expenses.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user   = SessionUtil.getLoggedInUser(request);
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            handleAddExpense(request, response, user);
        } else if ("edit".equals(action)) {
            handleEditExpense(request, response, user);
        } else if ("delete".equals(action)) {
            handleDeleteExpense(request, response, user);
        } else {
            response.sendRedirect(request.getContextPath() + "/student/expenses");
        }
    }

    // ============================================================
    // Handler Methods
    // ============================================================

    private void handleAddExpense(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {

        // Extract form data
        String title       = ValidationUtil.clean(request.getParameter("title"));
        String description = ValidationUtil.clean(request.getParameter("description"));
        double amount      = ValidationUtil.parseDoubleSafe(request.getParameter("amount"), 0);
        String dateStr     = request.getParameter("expenseDate");
        int    categoryId  = ValidationUtil.parseIntSafe(request.getParameter("categoryId"), 0);
        String splitType   = ValidationUtil.clean(request.getParameter("splitType"));
        String[] sharedWith = request.getParameterValues("sharedWith");

        // Validate
        if (ValidationUtil.isEmpty(title) || amount <= 0 || categoryId <= 0 || 
            ValidationUtil.isEmpty(dateStr)) {
            SessionUtil.setFlashMessage(request, "error", "Please fill all required fields.");
            response.sendRedirect(request.getContextPath() + "/student/expenses");
            return;
        }

        // Create expense
        Expense expense = new Expense();
        expense.setTitle(title);
        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setExpenseDate(java.sql.Date.valueOf(dateStr));
        expense.setPaidBy(user.getId());
        expense.setCategoryId(categoryId);
        expense.setSplitType(ValidationUtil.isEmpty(splitType) ? "EQUAL" : splitType);

        int expenseId = expenseDAO.addExpense(expense);

        if (expenseId > 0) {
            // Create shares
            List<Integer> shareUserIds = new ArrayList<>();
            shareUserIds.add(user.getId()); // Payer is always included

            if (sharedWith != null) {
                for (String uid : sharedWith) {
                    int id = ValidationUtil.parseIntSafe(uid, 0);
                    if (id > 0 && id != user.getId()) {
                        shareUserIds.add(id);
                    }
                }
            }

            int[] userIds = shareUserIds.stream().mapToInt(i -> i).toArray();
            double[] amounts;

            if ("CUSTOM".equals(splitType)) {
                // Custom split amounts
                amounts = new double[userIds.length];
                for (int i = 0; i < userIds.length; i++) {
                    amounts[i] = ValidationUtil.parseDoubleSafe(
                        request.getParameter("customAmount_" + userIds[i]), 
                        amount / userIds.length
                    );
                }
            } else {
                // Equal split
                double perPerson = Math.round((amount / userIds.length) * 100.0) / 100.0;
                amounts = new double[userIds.length];
                double total = 0;
                for (int i = 0; i < amounts.length - 1; i++) {
                    amounts[i] = perPerson;
                    total += perPerson;
                }
                // Last person gets remainder to avoid rounding errors
                amounts[amounts.length - 1] = Math.round((amount - total) * 100.0) / 100.0;
            }

            shareDAO.addShares(expenseId, user.getId(), userIds, amounts);

            // Send notifications to shared users
            List<Integer> notifyIds = new ArrayList<>();
            for (int uid : userIds) {
                if (uid != user.getId()) {
                    notifyIds.add(uid);
                }
            }
            if (!notifyIds.isEmpty()) {
                double share = amount / userIds.length;
                String msg = user.getFullName() + " added expense \"" + title + 
                             "\" - Your share: ₹" + String.format("%,.2f", share);
                notificationDAO.notifyUsers(notifyIds, msg, "EXPENSE_ADDED", 
                    "/student/expenses?action=view&id=" + expenseId);
            }

            SessionUtil.setFlashMessage(request, "success", "Expense added successfully!");
        } else {
            SessionUtil.setFlashMessage(request, "error", "Failed to add expense. Please try again.");
        }

        response.sendRedirect(request.getContextPath() + "/student/expenses");
    }

    private void handleEditExpense(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {

        int expenseId = ValidationUtil.parseIntSafe(request.getParameter("expenseId"), 0);
        Expense existing = expenseDAO.findById(expenseId);

        // Verify ownership
        if (existing == null || existing.getPaidBy() != user.getId()) {
            SessionUtil.setFlashMessage(request, "error", "You can only edit your own expenses.");
            response.sendRedirect(request.getContextPath() + "/student/expenses");
            return;
        }

        // Update fields
        existing.setTitle(ValidationUtil.clean(request.getParameter("title")));
        existing.setDescription(ValidationUtil.clean(request.getParameter("description")));
        existing.setAmount(ValidationUtil.parseDoubleSafe(request.getParameter("amount"), existing.getAmount()));
        String dateStr = request.getParameter("expenseDate");
        if (ValidationUtil.isNotEmpty(dateStr)) {
            existing.setExpenseDate(java.sql.Date.valueOf(dateStr));
        }
        existing.setCategoryId(ValidationUtil.parseIntSafe(request.getParameter("categoryId"), existing.getCategoryId()));

        if (expenseDAO.updateExpense(existing)) {
            // Update shares if needed
            String[] sharedWith = request.getParameterValues("sharedWith");
            if (sharedWith != null) {
                shareDAO.deleteSharesByExpense(expenseId);

                List<Integer> shareUserIds = new ArrayList<>();
                shareUserIds.add(user.getId());
                for (String uid : sharedWith) {
                    int id = ValidationUtil.parseIntSafe(uid, 0);
                    if (id > 0 && id != user.getId()) {
                        shareUserIds.add(id);
                    }
                }

                int[] userIds = shareUserIds.stream().mapToInt(i -> i).toArray();
                double perPerson = Math.round((existing.getAmount() / userIds.length) * 100.0) / 100.0;
                double[] amounts = new double[userIds.length];
                double total = 0;
                for (int i = 0; i < amounts.length - 1; i++) {
                    amounts[i] = perPerson;
                    total += perPerson;
                }
                amounts[amounts.length - 1] = Math.round((existing.getAmount() - total) * 100.0) / 100.0;

                shareDAO.addShares(expenseId, user.getId(), userIds, amounts);
            }

            SessionUtil.setFlashMessage(request, "success", "Expense updated successfully!");
        } else {
            SessionUtil.setFlashMessage(request, "error", "Failed to update expense.");
        }

        response.sendRedirect(request.getContextPath() + "/student/expenses");
    }

    private void handleDeleteExpense(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {

        int expenseId = ValidationUtil.parseIntSafe(request.getParameter("expenseId"), 0);
        Expense existing = expenseDAO.findById(expenseId);

        if (existing == null || existing.getPaidBy() != user.getId()) {
            SessionUtil.setFlashMessage(request, "error", "You can only delete your own expenses.");
        } else if (expenseDAO.deleteExpense(expenseId)) {
            SessionUtil.setFlashMessage(request, "success", "Expense deleted successfully!");
        } else {
            SessionUtil.setFlashMessage(request, "error", "Failed to delete expense.");
        }

        response.sendRedirect(request.getContextPath() + "/student/expenses");
    }

    private void handleViewExpense(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int expenseId = ValidationUtil.parseIntSafe(request.getParameter("id"), 0);
        Expense expense = expenseDAO.findById(expenseId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (expense == null) {
            out.print("{\"error\":\"Expense not found\"}");
            return;
        }

        // Get shares for this expense
        List<ExpenseShare> shares = shareDAO.getSharesByExpense(expenseId);

        // Build JSON response
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"id\":").append(expense.getId()).append(",");
        json.append("\"title\":\"").append(escapeJson(expense.getTitle())).append("\",");
        json.append("\"description\":\"").append(escapeJson(expense.getDescription() != null ? expense.getDescription() : "")).append("\",");
        json.append("\"amount\":").append(expense.getAmount()).append(",");
        json.append("\"expenseDate\":\"").append(expense.getExpenseDate()).append("\",");
        json.append("\"paidBy\":").append(expense.getPaidBy()).append(",");
        json.append("\"paidByName\":\"").append(escapeJson(expense.getPaidByName())).append("\",");
        json.append("\"categoryId\":").append(expense.getCategoryId()).append(",");
        json.append("\"categoryName\":\"").append(escapeJson(expense.getCategoryName())).append("\",");
        json.append("\"categoryIcon\":\"").append(escapeJson(expense.getCategoryIcon())).append("\",");
        json.append("\"splitType\":\"").append(expense.getSplitType()).append("\",");
        json.append("\"shares\":[");

        for (int i = 0; i < shares.size(); i++) {
            ExpenseShare share = shares.get(i);
            if (i > 0) json.append(",");
            json.append("{");
            json.append("\"userId\":").append(share.getUserId()).append(",");
            json.append("\"userName\":\"").append(escapeJson(share.getUserName())).append("\",");
            json.append("\"shareAmount\":").append(share.getShareAmount()).append(",");
            json.append("\"paid\":").append(share.isPaid());
            json.append("}");
        }

        json.append("]}");
        out.print(json.toString());
    }

    /** Escape special characters for JSON strings */
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}
