<%-- HostelMate — Expenses Page --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hostelmate.model.*" %>
<%@ page import="com.hostelmate.util.SessionUtil" %>
<%@ page import="java.util.List" %>
<%
    User expUser = SessionUtil.getLoggedInUser(request);
    request.setAttribute("pageTitle", "My Expenses");
    List<Expense> expenses = (List<Expense>) request.getAttribute("expenses");
    List<Category> categories = (List<Category>) request.getAttribute("categories");
    List<User> students = (List<User>) request.getAttribute("students");
    List<User> roommates = (List<User>) request.getAttribute("roommates");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Expenses — HostelMate</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
</head>
<body>
    <%@ include file="/jsp/common/sidebar.jsp" %>
    <div class="main-content">
        <%@ include file="/jsp/common/header.jsp" %>
        <div class="content-area">
            <!-- Filter Bar -->
            <form class="filter-bar" method="GET" action="<%= request.getContextPath() %>/student/expenses">
                <div class="form-group" style="flex:2">
                    <label>Search</label>
                    <input type="text" class="form-control" name="search" placeholder="Search expenses..." 
                           value="<%= request.getParameter("search") != null ? request.getParameter("search") : "" %>">
                </div>
                <div class="form-group">
                    <label>Category</label>
                    <select class="form-select" name="category">
                        <option value="">All Categories</option>
                        <% if (categories != null) for (Category c : categories) { %>
                            <option value="<%= c.getId() %>" 
                                <%= String.valueOf(c.getId()).equals(request.getParameter("category")) ? "selected" : "" %>>
                                <%= c.getName() %>
                            </option>
                        <% } %>
                    </select>
                </div>
                <div class="form-group">
                    <label>From</label>
                    <input type="date" class="form-control" name="startDate" 
                           value="<%= request.getParameter("startDate") != null ? request.getParameter("startDate") : "" %>">
                </div>
                <div class="form-group">
                    <label>To</label>
                    <input type="date" class="form-control" name="endDate" 
                           value="<%= request.getParameter("endDate") != null ? request.getParameter("endDate") : "" %>">
                </div>
                <div class="form-group" style="flex:0">
                    <label>&nbsp;</label>
                    <button type="submit" class="btn-primary-gradient"><i class="bi bi-search"></i> Filter</button>
                </div>
            </form>

            <!-- Add Expense Button -->
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h3 style="font-weight:700;font-size:var(--font-size-lg);">
                    <%= expenses != null ? expenses.size() : 0 %> Expenses
                </h3>
                <button class="btn-primary-gradient" data-bs-toggle="modal" data-bs-target="#addExpenseModal">
                    <i class="bi bi-plus-lg"></i> Add Expense
                </button>
            </div>

            <!-- Expenses Table -->
            <div class="content-card">
                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Expense</th>
                                <th>Category</th>
                                <th>Amount</th>
                                <th>Date</th>
                                <th>Paid By</th>
                                <th>Split</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                        <% if (expenses != null && !expenses.isEmpty()) { 
                            for (Expense exp : expenses) { %>
                            <tr>
                                <td>
                                    <strong><%= exp.getTitle() %></strong>
                                    <% if (exp.getDescription() != null && !exp.getDescription().isEmpty()) { %>
                                        <div style="font-size:12px;color:var(--text-muted);margin-top:2px"><%= exp.getDescription().length() > 50 ? exp.getDescription().substring(0, 50) + "..." : exp.getDescription() %></div>
                                    <% } %>
                                </td>
                                <td><span class="category-badge"><i class="bi <%= exp.getCategoryIcon() %>"></i> <%= exp.getCategoryName() %></span></td>
                                <td class="amount">₹<%= String.format("%,.2f", exp.getAmount()) %></td>
                                <td><%= exp.getExpenseDate() %></td>
                                <td><%= exp.getPaidByName() %></td>
                                <td><span class="badge-status badge-paid"><%= exp.getShareCount() %> people</span></td>
                                <td>
                                    <button class="btn-sm-action" onclick="viewExpense(<%= exp.getId() %>)" title="View">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                    <% if (exp.getPaidBy() == expUser.getId()) { %>
                                    <button class="btn-sm-action" onclick="editExpense(<%= exp.getId() %>)" title="Edit">
                                        <i class="bi bi-pencil"></i>
                                    </button>
                                    <button class="btn-sm-action btn-danger" onclick="confirmDelete(<%= exp.getId() %>)" title="Delete">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                    <% } %>
                                </td>
                            </tr>
                        <% } } else { %>
                            <tr><td colspan="7">
                                <div class="empty-state">
                                    <i class="bi bi-receipt-cutoff"></i>
                                    <p>No expenses found</p>
                                </div>
                            </td></tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Add Expense Modal -->
    <div class="modal fade" id="addExpenseModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" style="font-weight:700"><i class="bi bi-plus-circle me-2"></i>Add New Expense</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form method="POST" action="<%= request.getContextPath() %>/student/expenses">
                    <input type="hidden" name="action" value="add">
                    <div class="modal-body">
                        <div class="row g-3">
                            <div class="col-md-8">
                                <label class="form-label">Title *</label>
                                <input type="text" class="form-control" name="title" required placeholder="e.g., WiFi Bill May">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Amount (₹) *</label>
                                <input type="number" class="form-control" name="amount" required min="1" step="0.01" placeholder="0.00">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Category *</label>
                                <select class="form-select" name="categoryId" required>
                                    <option value="">Select category</option>
                                    <% if (categories != null) for (Category c : categories) { %>
                                        <option value="<%= c.getId() %>"><%= c.getName() %></option>
                                    <% } %>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Date *</label>
                                <input type="date" class="form-control" name="expenseDate" required>
                            </div>
                            <div class="col-12">
                                <label class="form-label">Description</label>
                                <textarea class="form-control" name="description" rows="2" placeholder="Optional description"></textarea>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Split Type</label>
                                <select class="form-select" name="splitType">
                                    <option value="EQUAL">Equal Split</option>
                                    <option value="CUSTOM">Custom Split</option>
                                </select>
                            </div>
                            <div class="col-12">
                                <label class="form-label">Share With</label>
                                <div class="row g-2">
                                    <% if (students != null) for (User s : students) { 
                                        if (s.getId() == expUser.getId()) continue; %>
                                    <div class="col-md-4">
                                        <div class="form-check">
                                            <input class="form-check-input" type="checkbox" name="sharedWith" 
                                                   value="<%= s.getId() %>" id="share_<%= s.getId() %>"
                                                   <%= (roommates != null && roommates.stream().anyMatch(r -> r.getId() == s.getId())) ? "checked" : "" %>>
                                            <label class="form-check-label" for="share_<%= s.getId() %>" style="font-size:13px">
                                                <%= s.getFullName() %>
                                                <% if (s.getRoomNumber() != null) { %>
                                                    <span style="color:var(--text-muted);font-size:11px">(Room <%= s.getRoomNumber() %>)</span>
                                                <% } %>
                                            </label>
                                        </div>
                                    </div>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn-primary-gradient">
                            <i class="bi bi-check-lg"></i> Add Expense
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- View Expense Modal -->
    <div class="modal fade" id="viewExpenseModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" style="font-weight:700"><i class="bi bi-receipt me-2"></i>Expense Details</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <h4 id="viewTitle" style="font-weight:800;margin-bottom:4px"></h4>
                    <p id="viewDescription" style="color:var(--text-muted);font-size:14px;margin-bottom:16px"></p>
                    <div class="row g-3 mb-3">
                        <div class="col-6"><strong>Amount:</strong> <span id="viewAmount" class="amount" style="color:var(--primary-600)"></span></div>
                        <div class="col-6"><strong>Date:</strong> <span id="viewDate"></span></div>
                        <div class="col-6"><strong>Category:</strong> <span id="viewCategory"></span></div>
                        <div class="col-6"><strong>Paid By:</strong> <span id="viewPaidBy"></span></div>
                        <div class="col-6"><strong>Split Type:</strong> <span id="viewSplitType"></span></div>
                    </div>
                    <h6 style="font-weight:700;margin-top:16px;margin-bottom:8px">Shares</h6>
                    <div id="viewShares"></div>
                </div>
            </div>
        </div>
    </div>

    <!-- Edit Expense Modal -->
    <div class="modal fade" id="editExpenseModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" style="font-weight:700"><i class="bi bi-pencil-square me-2"></i>Edit Expense</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form method="POST" action="<%= request.getContextPath() %>/student/expenses">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="expenseId" id="editExpenseId">
                    <div class="modal-body">
                        <div class="row g-3">
                            <div class="col-md-8">
                                <label class="form-label">Title *</label>
                                <input type="text" class="form-control" name="title" id="editTitle" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Amount (₹) *</label>
                                <input type="number" class="form-control" name="amount" id="editAmount" required min="1" step="0.01">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Category *</label>
                                <select class="form-select" name="categoryId" id="editCategory" required>
                                    <% if (categories != null) for (Category c : categories) { %>
                                        <option value="<%= c.getId() %>"><%= c.getName() %></option>
                                    <% } %>
                                </select>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Date *</label>
                                <input type="date" class="form-control" name="expenseDate" id="editDate" required>
                            </div>
                            <div class="col-12">
                                <label class="form-label">Description</label>
                                <textarea class="form-control" name="description" id="editDescription" rows="2"></textarea>
                            </div>
                            <div class="col-12">
                                <label class="form-label">Share With</label>
                                <div class="row g-2">
                                    <% if (students != null) for (User s : students) { 
                                        if (s.getId() == expUser.getId()) continue; %>
                                    <div class="col-md-4">
                                        <div class="form-check">
                                            <input class="form-check-input" type="checkbox" name="sharedWith" value="<%= s.getId() %>">
                                            <label class="form-check-label" style="font-size:13px"><%= s.getFullName() %></label>
                                        </div>
                                    </div>
                                    <% } %>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button>
                        <button type="submit" class="btn-primary-gradient"><i class="bi bi-check-lg"></i> Save Changes</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <%@ include file="/jsp/common/footer.jsp" %>
