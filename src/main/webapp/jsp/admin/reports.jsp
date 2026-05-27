<%-- HostelMate — Admin Reports --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hostelmate.model.*" %>
<%@ page import="java.util.List" %>
<% request.setAttribute("pageTitle", "Reports");
   List<Expense> repExp = (List<Expense>) request.getAttribute("expenses");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reports — HostelMate Admin</title>
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
            <div class="content-card"><div class="card-header"><h3><i class="bi bi-file-earmark-bar-graph me-2"></i>All Expenses Report</h3></div>
                <div class="card-body"><div class="table-responsive"><table class="data-table">
                    <thead><tr><th>#</th><th>Title</th><th>Category</th><th>Amount</th><th>Date</th><th>Paid By</th><th>Split</th></tr></thead>
                    <tbody>
                    <% int idx=1; double total=0; if (repExp != null) for (Expense e : repExp) { total += e.getAmount(); %>
                    <tr><td><%= idx++ %></td><td><%= e.getTitle() %></td>
                        <td><span class="category-badge"><i class="bi <%= e.getCategoryIcon() %>"></i> <%= e.getCategoryName() %></span></td>
                        <td class="amount">₹<%= String.format("%,.2f", e.getAmount()) %></td>
                        <td><%= e.getExpenseDate() %></td><td><%= e.getPaidByName() %></td>
                        <td><%= e.getShareCount() %> people</td></tr>
                    <% } %>
                    </tbody>
                    <tfoot><tr><td colspan="3" style="font-weight:800;text-align:right">Grand Total:</td><td class="amount" style="font-size:16px;color:var(--primary-600)">₹<%= String.format("%,.2f", total) %></td><td colspan="3"></td></tr></tfoot>
                </table></div></div>
            </div>
        </div>
    </div>
    <%@ include file="/jsp/common/footer.jsp" %>
