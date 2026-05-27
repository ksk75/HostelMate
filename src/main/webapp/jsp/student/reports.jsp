<%-- HostelMate — Student Reports Page --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hostelmate.model.*" %>
<%@ page import="com.hostelmate.util.SessionUtil" %>
<%@ page import="java.util.List" %>
<% request.setAttribute("pageTitle", "Reports & Analytics"); 
   List<Object[]> catBreak = (List<Object[]>) request.getAttribute("categoryBreakdown");
   List<Object[]> monthTrend = (List<Object[]>) request.getAttribute("monthlyTrend");
   List<Expense> repExpenses = (List<Expense>) request.getAttribute("expenses");
   String[] mNames = {"", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reports — HostelMate</title>
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
                <div class="col-lg-8">
                    <div class="content-card"><div class="card-header"><h3><i class="bi bi-bar-chart-line me-2"></i>Category-wise Breakdown</h3></div>
                        <div class="card-body"><div class="chart-container"><canvas id="catBarChart"></canvas></div></div>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="content-card"><div class="card-header"><h3><i class="bi bi-pie-chart me-2"></i>Distribution</h3></div>
                        <div class="card-body"><div class="chart-container" style="height:280px"><canvas id="catPieChart"></canvas></div></div>
                    </div>
                </div>
                <div class="col-12">
                    <div class="content-card"><div class="card-header"><h3><i class="bi bi-graph-up me-2"></i>Monthly Trend</h3></div>
                        <div class="card-body"><div class="chart-container"><canvas id="trendLineChart"></canvas></div></div>
                    </div>
                </div>
                <!-- Expense Summary Table -->
                <div class="col-12">
                    <div class="content-card"><div class="card-header"><h3><i class="bi bi-table me-2"></i>Expense Summary</h3></div>
                        <div class="card-body"><div class="table-responsive"><table class="data-table">
                            <thead><tr><th>Title</th><th>Category</th><th>Amount</th><th>Date</th><th>Paid By</th></tr></thead>
                            <tbody>
                            <% if (repExpenses != null) for (Expense e : repExpenses) { %>
                            <tr><td><%= e.getTitle() %></td><td><span class="category-badge"><i class="bi <%= e.getCategoryIcon() %>"></i> <%= e.getCategoryName() %></span></td>
                                <td class="amount">₹<%= String.format("%,.2f", e.getAmount()) %></td><td><%= e.getExpenseDate() %></td><td><%= e.getPaidByName() %></td></tr>
                            <% } %>
                            </tbody></table></div></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.4/dist/chart.umd.min.js"></script>
    <script src="<%= request.getContextPath() %>/js/charts.js"></script>
    <script>
    <% if (catBreak != null) { %>
        initBarChart('catBarChart',
            [<% for(int i=0;i<catBreak.size();i++){if(i>0)out.print(",");out.print("\""+catBreak.get(i)[0]+"\"");} %>],
            [<% for(int i=0;i<catBreak.size();i++){if(i>0)out.print(",");out.print(catBreak.get(i)[2]);} %>],
            'Expenses by Category');
        initCategoryChart('catPieChart',
            [<% for(int i=0;i<catBreak.size();i++){if(i>0)out.print(",");out.print("\""+catBreak.get(i)[0]+"\"");} %>],
            [<% for(int i=0;i<catBreak.size();i++){if(i>0)out.print(",");out.print(catBreak.get(i)[2]);} %>]);
    <% } if (monthTrend != null) { %>
        initTrendChart('trendLineChart',
            [<% for(int i=0;i<monthTrend.size();i++){if(i>0)out.print(",");out.print("\""+mNames[(int)monthTrend.get(i)[0]]+" "+monthTrend.get(i)[1]+"\"");} %>],
            [<% for(int i=0;i<monthTrend.size();i++){if(i>0)out.print(",");out.print(monthTrend.get(i)[2]);} %>]);
    <% } %>
    </script>
    <%@ include file="/jsp/common/footer.jsp" %>
