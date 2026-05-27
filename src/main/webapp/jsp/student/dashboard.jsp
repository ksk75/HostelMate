<%-- HostelMate — Student Dashboard --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hostelmate.model.*" %>
<%@ page import="com.hostelmate.util.SessionUtil" %>
<%@ page import="java.util.List" %>
<%
    User dashUser = SessionUtil.getLoggedInUser(request);
    request.setAttribute("pageTitle", "Dashboard");
    double monthlyExp = request.getAttribute("monthlyExpenses") != null ? (double) request.getAttribute("monthlyExpenses") : 0;
    double pendingDues = request.getAttribute("pendingDues") != null ? (double) request.getAttribute("pendingDues") : 0;
    double totalPaid = request.getAttribute("totalPaid") != null ? (double) request.getAttribute("totalPaid") : 0;
    double totalExp = request.getAttribute("totalExpenses") != null ? (double) request.getAttribute("totalExpenses") : 0;
    List<Expense> recentExp = (List<Expense>) request.getAttribute("recentExpenses");
    List<Object[]> balances = (List<Object[]>) request.getAttribute("balances");
    String catLabels = (String) request.getAttribute("categoryLabels");
    String catData = (String) request.getAttribute("categoryData");
    String trendLabels = (String) request.getAttribute("trendLabels");
    String trendData = (String) request.getAttribute("trendData");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard — HostelMate</title>
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
            <!-- Welcome -->
            <div class="mb-4">
                <h2 style="font-size:var(--font-size-2xl);font-weight:800;margin-bottom:4px;">
                    Welcome back, <%= dashUser.getFirstName() %> 👋
                </h2>
                <p style="color:var(--text-secondary);font-size:var(--font-size-sm);">
                    Here's your expense overview for this month
                </p>
            </div>

            <!-- Stat Cards -->
            <div class="row g-4 mb-4">
                <div class="col-md-6 col-xl-3 animate-fade-in animate-delay-1">
                    <div class="stat-card card-primary">
                        <div class="card-icon icon-primary"><i class="bi bi-wallet2"></i></div>
                        <div class="card-label">Monthly Expenses</div>
                        <div class="card-value">₹<%= String.format("%,.0f", monthlyExp) %></div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-3 animate-fade-in animate-delay-2">
                    <div class="stat-card card-danger">
                        <div class="card-icon icon-danger"><i class="bi bi-exclamation-triangle"></i></div>
                        <div class="card-label">Pending Dues</div>
                        <div class="card-value">₹<%= String.format("%,.0f", pendingDues) %></div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-3 animate-fade-in animate-delay-3">
                    <div class="stat-card card-success">
                        <div class="card-icon icon-success"><i class="bi bi-check-circle"></i></div>
                        <div class="card-label">Total Paid</div>
                        <div class="card-value">₹<%= String.format("%,.0f", totalPaid) %></div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-3 animate-fade-in animate-delay-4">
                    <div class="stat-card card-info">
                        <div class="card-icon icon-info"><i class="bi bi-receipt"></i></div>
                        <div class="card-label">Total Expenses</div>
                        <div class="card-value">₹<%= String.format("%,.0f", totalExp) %></div>
                    </div>
                </div>
            </div>

            <div class="row g-4">
                <!-- Monthly Trend Chart -->
                <div class="col-lg-8">
                    <div class="content-card">
                        <div class="card-header">
                            <h3><i class="bi bi-graph-up-arrow me-2"></i>Monthly Trend</h3>
                        </div>
                        <div class="card-body">
                            <div class="chart-container">
                                <canvas id="trendChart"></canvas>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Category Breakdown -->
                <div class="col-lg-4">
                    <div class="content-card">
                        <div class="card-header">
                            <h3><i class="bi bi-pie-chart me-2"></i>By Category</h3>
                        </div>
                        <div class="card-body">
                            <div class="chart-container" style="height:250px">
                                <canvas id="categoryChart"></canvas>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Balances -->
                <div class="col-lg-4">
                    <div class="content-card">
                        <div class="card-header">
                            <h3><i class="bi bi-arrow-left-right me-2"></i>Balances</h3>
                            <a href="<%= request.getContextPath() %>/student/settlements" class="btn-sm-action">
                                View All <i class="bi bi-chevron-right"></i>
                            </a>
                        </div>
                        <div class="card-body">
                            <% if (balances != null && !balances.isEmpty()) {
                                for (Object[] b : balances) {
                                    double netAmount = (double) b[2];
                                    if (Math.abs(netAmount) < 0.01) continue;
                            %>
                                <div class="balance-card <%= netAmount > 0 ? "balance-positive" : "balance-negative" %>">
                                    <div>
                                        <div class="balance-name"><%= b[1] %></div>
                                        <div style="font-size:11px;color:var(--text-muted)">
                                            <%= netAmount > 0 ? "owes you" : "you owe" %>
                                        </div>
                                    </div>
                                    <div class="balance-amount <%= netAmount > 0 ? "text-success" : "text-danger" %>">
                                        ₹<%= String.format("%,.2f", Math.abs(netAmount)) %>
                                    </div>
                                </div>
                            <% } } else { %>
                                <div class="empty-state">
                                    <i class="bi bi-check-all"></i>
                                    <p>All settled! No pending balances.</p>
                                </div>
                            <% } %>
                        </div>
                    </div>
                </div>

                <!-- Recent Transactions -->
                <div class="col-lg-8">
                    <div class="content-card">
                        <div class="card-header">
                            <h3><i class="bi bi-clock-history me-2"></i>Recent Transactions</h3>
                            <a href="<%= request.getContextPath() %>/student/expenses" class="btn-sm-action">
                                View All <i class="bi bi-chevron-right"></i>
                            </a>
                        </div>
                        <div class="card-body">
                            <% if (recentExp != null && !recentExp.isEmpty()) { 
                                for (Expense exp : recentExp) { %>
                                <div class="transaction-item">
                                    <div class="transaction-icon">
                                        <i class="bi <%= exp.getCategoryIcon() %>"></i>
                                    </div>
                                    <div class="transaction-details">
                                        <div class="transaction-title"><%= exp.getTitle() %></div>
                                        <div class="transaction-meta">
                                            <i class="bi bi-person-fill"></i> <%= exp.getPaidByName() %> · 
                                            <i class="bi bi-calendar3"></i> <%= exp.getExpenseDate() %> ·
                                            <span class="category-badge"><i class="bi <%= exp.getCategoryIcon() %>"></i> <%= exp.getCategoryName() %></span>
                                        </div>
                                    </div>
                                    <div class="transaction-amount">₹<%= String.format("%,.2f", exp.getAmount()) %></div>
                                </div>
                            <% } } else { %>
                                <div class="empty-state">
                                    <i class="bi bi-receipt-cutoff"></i>
                                    <p>No expenses yet</p>
                                </div>
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Chart.js -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"></script>
    <script src="<%= request.getContextPath() %>/js/charts.js"></script>
    <script>
        // Initialize charts with data from server
        initCategoryChart('categoryChart', <%= catLabels != null ? catLabels : "[]" %>, <%= catData != null ? catData : "[]" %>);
        initTrendChart('trendChart', <%= trendLabels != null ? trendLabels : "[]" %>, <%= trendData != null ? trendData : "[]" %>);
    </script>
    <%@ include file="/jsp/common/footer.jsp" %>
