<%-- HostelMate — Admin Dashboard --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hostelmate.model.*" %>
<%@ page import="com.hostelmate.util.SessionUtil" %>
<%@ page import="java.util.List" %>
<% request.setAttribute("pageTitle", "Admin Dashboard");
   int totalStudents = request.getAttribute("totalStudents") != null ? (int) request.getAttribute("totalStudents") : 0;
   int totalRooms = request.getAttribute("totalRooms") != null ? (int) request.getAttribute("totalRooms") : 0;
   double totalExp = request.getAttribute("totalExpenses") != null ? (double) request.getAttribute("totalExpenses") : 0;
   double monthCol = request.getAttribute("monthlyCollection") != null ? (double) request.getAttribute("monthlyCollection") : 0;
   double pendPay = request.getAttribute("pendingPayments") != null ? (double) request.getAttribute("pendingPayments") : 0;
   List<Expense> allExp = (List<Expense>) request.getAttribute("recentExpenses");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard — HostelMate</title>
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
            <div class="mb-4"><h2 style="font-size:var(--font-size-2xl);font-weight:800">Admin Overview</h2></div>
            <div class="row g-4 mb-4">
                <div class="col-md-6 col-xl animate-fade-in animate-delay-1">
                    <div class="stat-card card-primary"><div class="card-icon icon-primary"><i class="bi bi-people"></i></div>
                    <div class="card-label">Total Students</div><div class="card-value"><%= totalStudents %></div></div>
                </div>
                <div class="col-md-6 col-xl animate-fade-in animate-delay-2">
                    <div class="stat-card card-accent"><div class="card-icon icon-accent"><i class="bi bi-door-open"></i></div>
                    <div class="card-label">Total Rooms</div><div class="card-value"><%= totalRooms %></div></div>
                </div>
                <div class="col-md-6 col-xl animate-fade-in animate-delay-3">
                    <div class="stat-card card-success"><div class="card-icon icon-success"><i class="bi bi-currency-rupee"></i></div>
                    <div class="card-label">Total Expenses</div><div class="card-value">₹<%= String.format("%,.0f", totalExp) %></div></div>
                </div>
                <div class="col-md-6 col-xl animate-fade-in animate-delay-4">
                    <div class="stat-card card-info"><div class="card-icon icon-info"><i class="bi bi-calendar-month"></i></div>
                    <div class="card-label">Monthly Collection</div><div class="card-value">₹<%= String.format("%,.0f", monthCol) %></div></div>
                </div>
                <div class="col-md-6 col-xl animate-fade-in">
                    <div class="stat-card card-warning"><div class="card-icon icon-warning"><i class="bi bi-hourglass-split"></i></div>
                    <div class="card-label">Pending Payments</div><div class="card-value">₹<%= String.format("%,.0f", pendPay) %></div></div>
                </div>
            </div>
            <!-- Quick Links -->
            <div class="row g-3 mb-4">
                <div class="col-auto"><a href="<%= request.getContextPath() %>/admin/users" class="btn-primary-gradient"><i class="bi bi-people"></i> Manage Users</a></div>
                <div class="col-auto"><a href="<%= request.getContextPath() %>/admin/rooms" class="btn-primary-gradient" style="background:linear-gradient(135deg,var(--accent-500),var(--accent-600))"><i class="bi bi-door-open"></i> Manage Rooms</a></div>
                <div class="col-auto"><a href="<%= request.getContextPath() %>/admin/reports" class="btn-primary-gradient" style="background:linear-gradient(135deg,var(--warning),#d97706)"><i class="bi bi-file-earmark-bar-graph"></i> View Reports</a></div>
            </div>
            <!-- All Expenses -->
            <div class="content-card"><div class="card-header"><h3><i class="bi bi-receipt me-2"></i>All Expenses</h3></div>
                <div class="card-body"><div class="table-responsive"><table class="data-table">
                    <thead><tr><th>Title</th><th>Category</th><th>Amount</th><th>Date</th><th>Paid By</th><th>Shared</th></tr></thead>
                    <tbody>
                    <% if (allExp != null) for (Expense e : allExp) { %>
                    <tr><td><strong><%= e.getTitle() %></strong></td>
                        <td><span class="category-badge"><i class="bi <%= e.getCategoryIcon() %>"></i> <%= e.getCategoryName() %></span></td>
                        <td class="amount">₹<%= String.format("%,.2f", e.getAmount()) %></td>
                        <td><%= e.getExpenseDate() %></td><td><%= e.getPaidByName() %></td>
                        <td><%= e.getShareCount() %> people</td></tr>
                    <% } %>
                    </tbody></table></div></div>
            </div>
        </div>
    </div>
    <%@ include file="/jsp/common/footer.jsp" %>
