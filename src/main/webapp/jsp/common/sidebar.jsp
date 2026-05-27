<%-- 
    HostelMate — Sidebar Navigation Include
    Included in every authenticated page.
    Renders different navigation items based on user role.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hostelmate.model.User" %>
<%@ page import="com.hostelmate.util.SessionUtil" %>
<%
    User sidebarUser = SessionUtil.getLoggedInUser(request);
    boolean isAdmin = sidebarUser != null && sidebarUser.isAdmin();
    String sidebarURI = request.getServletPath();
%>

<aside class="sidebar" id="sidebar">
    <!-- Brand -->
    <div class="sidebar-brand">
        <div class="brand-icon">
            <i class="bi bi-house-heart"></i>
        </div>
        <div class="brand-text">Hostel<span>Mate</span></div>
    </div>

    <!-- Navigation -->
    <nav class="sidebar-nav">
        <% if (isAdmin) { %>
            <!-- Admin Navigation -->
            <div class="nav-section">Main</div>
            <a href="<%= request.getContextPath() %>/admin/dashboard" 
               class="nav-link <%= "/admin/dashboard".equals(sidebarURI) ? "active" : "" %>">
                <i class="bi bi-grid-1x2"></i> Dashboard
            </a>

            <div class="nav-section">Management</div>
            <a href="<%= request.getContextPath() %>/admin/users" 
               class="nav-link <%= "/admin/users".equals(sidebarURI) ? "active" : "" %>">
                <i class="bi bi-people"></i> Users
            </a>
            <a href="<%= request.getContextPath() %>/admin/rooms" 
               class="nav-link <%= "/admin/rooms".equals(sidebarURI) ? "active" : "" %>">
                <i class="bi bi-door-open"></i> Rooms
            </a>

            <div class="nav-section">Reports</div>
            <a href="<%= request.getContextPath() %>/admin/reports" 
               class="nav-link <%= "/admin/reports".equals(sidebarURI) ? "active" : "" %>">
                <i class="bi bi-file-earmark-bar-graph"></i> Reports
            </a>
        <% } else { %>
            <!-- Student Navigation -->
            <div class="nav-section">Main</div>
            <a href="<%= request.getContextPath() %>/student/dashboard" 
               class="nav-link <%= "/student/dashboard".equals(sidebarURI) ? "active" : "" %>">
                <i class="bi bi-grid-1x2"></i> Dashboard
            </a>

            <div class="nav-section">Expenses</div>
            <a href="<%= request.getContextPath() %>/student/expenses" 
               class="nav-link <%= "/student/expenses".equals(sidebarURI) ? "active" : "" %>">
                <i class="bi bi-receipt"></i> My Expenses
            </a>
            <a href="<%= request.getContextPath() %>/student/settlements" 
               class="nav-link <%= "/student/settlements".equals(sidebarURI) ? "active" : "" %>">
                <i class="bi bi-arrow-left-right"></i> Settlements
            </a>

            <div class="nav-section">Analytics</div>
            <a href="<%= request.getContextPath() %>/student/reports" 
               class="nav-link <%= "/student/reports".equals(sidebarURI) ? "active" : "" %>">
                <i class="bi bi-bar-chart-line"></i> Reports
            </a>

            <div class="nav-section">Account</div>
            <a href="<%= request.getContextPath() %>/student/profile" 
               class="nav-link <%= "/student/profile".equals(sidebarURI) ? "active" : "" %>">
                <i class="bi bi-person-circle"></i> Profile
            </a>
        <% } %>
    </nav>

    <!-- Footer -->
    <div class="sidebar-footer">
        <div class="user-info">
            <div class="user-avatar">
                <%= sidebarUser != null ? sidebarUser.getInitials() : "?" %>
            </div>
            <div>
                <div class="user-name"><%= sidebarUser != null ? sidebarUser.getFirstName() : "User" %></div>
                <div class="user-role"><%= sidebarUser != null ? sidebarUser.getRole() : "" %></div>
            </div>
            <a href="<%= request.getContextPath() %>/logout" style="margin-left:auto;color:rgba(255,255,255,.5);font-size:18px;" title="Logout">
                <i class="bi bi-box-arrow-right"></i>
            </a>
        </div>
    </div>
</aside>
