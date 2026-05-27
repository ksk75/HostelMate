<%-- HostelMate — Settlements Page --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hostelmate.model.*" %>
<%@ page import="com.hostelmate.util.SessionUtil" %>
<%@ page import="java.util.List" %>
<%
    User settUser = SessionUtil.getLoggedInUser(request);
    request.setAttribute("pageTitle", "Settlements");
    List<Object[]> balances = (List<Object[]>) request.getAttribute("balances");
    List<ExpenseShare> pendingShares = (List<ExpenseShare>) request.getAttribute("pendingShares");
    List<Settlement> settlements = (List<Settlement>) request.getAttribute("settlements");
    List<User> students = (List<User>) request.getAttribute("students");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Settlements — HostelMate</title>
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
            <div class="row g-4">
                <!-- Net Balances -->
                <div class="col-lg-5">
                    <div class="content-card">
                        <div class="card-header">
                            <h3><i class="bi bi-arrow-left-right me-2"></i>Net Balances</h3>
                        </div>
                        <div class="card-body">
                            <% if (balances != null && !balances.isEmpty()) {
                                for (Object[] b : balances) {
                                    double net = (double) b[2];
                                    if (Math.abs(net) < 0.01) continue; %>
                                <div class="balance-card <%= net > 0 ? "balance-positive" : "balance-negative" %>">
                                    <div>
                                        <div class="balance-name"><%= b[1] %></div>
                                        <div style="font-size:11px;color:var(--text-muted)"><%= net > 0 ? "owes you" : "you owe" %></div>
                                    </div>
                                    <div class="d-flex align-items-center gap-2">
                                        <span class="balance-amount <%= net > 0 ? "text-success" : "text-danger" %>">
                                            ₹<%= String.format("%,.2f", Math.abs(net)) %>
                                        </span>
                                        <% if (net < 0) { %>
                                        <form method="POST" action="<%= request.getContextPath() %>/student/settlements" style="display:inline">
                                            <input type="hidden" name="action" value="create">
                                            <input type="hidden" name="toUserId" value="<%= b[0] %>">
                                            <input type="hidden" name="amount" value="<%= String.format("%.2f", Math.abs(net)) %>">
                                            <input type="hidden" name="notes" value="Settlement for pending balances">
                                            <button type="submit" class="btn-sm-action" title="Settle"><i class="bi bi-check-lg"></i> Settle</button>
                                        </form>
                                        <% } %>
                                    </div>
                                </div>
                            <% } } else { %>
                                <div class="empty-state"><i class="bi bi-check-all"></i><p>All settled!</p></div>
                            <% } %>
                        </div>
                    </div>

                    <!-- Pending Shares I Owe -->
                    <div class="content-card">
                        <div class="card-header"><h3><i class="bi bi-exclamation-circle me-2"></i>Pending Payments</h3></div>
                        <div class="card-body">
                            <% if (pendingShares != null && !pendingShares.isEmpty()) { for (ExpenseShare ps : pendingShares) { %>
                            <div class="transaction-item">
                                <div class="transaction-icon" style="background:#fee2e2;color:var(--danger)"><i class="bi bi-clock"></i></div>
                                <div class="transaction-details">
                                    <div class="transaction-title"><%= ps.getExpenseTitle() %></div>
                                    <div class="transaction-meta">Your share</div>
                                </div>
                                <div class="d-flex align-items-center gap-2">
                                    <span class="transaction-amount" style="color:var(--danger)">₹<%= String.format("%,.2f", ps.getShareAmount()) %></span>
                                    <form method="POST" action="<%= request.getContextPath() %>/student/settlements" style="display:inline">
                                        <input type="hidden" name="action" value="markPaid">
                                        <input type="hidden" name="shareId" value="<%= ps.getId() %>">
                                        <button type="submit" class="btn-sm-action" title="Mark Paid"><i class="bi bi-check"></i></button>
                                    </form>
                                </div>
                            </div>
                            <% } } else { %>
                                <div class="empty-state"><i class="bi bi-emoji-smile"></i><p>No pending payments</p></div>
                            <% } %>
                        </div>
                    </div>
                </div>

                <!-- Settlement History -->
                <div class="col-lg-7">
                    <div class="content-card">
                        <div class="card-header"><h3><i class="bi bi-clock-history me-2"></i>Settlement History</h3></div>
                        <div class="card-body">
                            <div class="table-responsive">
                                <table class="data-table">
                                    <thead><tr><th>From</th><th>To</th><th>Amount</th><th>Status</th><th>Date</th><th>Actions</th></tr></thead>
                                    <tbody>
                                    <% if (settlements != null && !settlements.isEmpty()) { for (Settlement s : settlements) { %>
                                    <tr>
                                        <td><%= s.getFromUserName() %></td>
                                        <td><%= s.getToUserName() %></td>
                                        <td class="amount">₹<%= String.format("%,.2f", s.getAmount()) %></td>
                                        <td><span class="badge-status badge-<%= s.getStatus().toLowerCase() %>"><%= s.getStatus() %></span></td>
                                        <td><%= s.getSettledDate() != null ? s.getSettledDate() : "Pending" %></td>
                                        <td>
                                        <% if (s.isPending()) { %>
                                            <form method="POST" action="<%= request.getContextPath() %>/student/settlements" style="display:inline">
                                                <input type="hidden" name="action" value="complete">
                                                <input type="hidden" name="settlementId" value="<%= s.getId() %>">
                                                <button type="submit" class="btn-sm-action" title="Complete"><i class="bi bi-check-circle"></i></button>
                                            </form>
                                            <form method="POST" action="<%= request.getContextPath() %>/student/settlements" style="display:inline">
                                                <input type="hidden" name="action" value="cancel">
                                                <input type="hidden" name="settlementId" value="<%= s.getId() %>">
                                                <button type="submit" class="btn-sm-action btn-danger" title="Cancel"><i class="bi bi-x-circle"></i></button>
                                            </form>
                                        <% } %>
                                        </td>
                                    </tr>
                                    <% } } else { %>
                                    <tr><td colspan="6"><div class="empty-state"><i class="bi bi-inbox"></i><p>No settlements yet</p></div></td></tr>
                                    <% } %>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%@ include file="/jsp/common/footer.jsp" %>
