<%-- 
    HostelMate — Common Header Include
    Included at the top of every authenticated page.
    Contains: Top navbar with search, notifications, theme toggle, user menu.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hostelmate.model.User" %>
<%@ page import="com.hostelmate.util.SessionUtil" %>
<%
    User headerUser = SessionUtil.getLoggedInUser(request);
    String[] flash = SessionUtil.getFlashMessage(request);
    String currentURI = request.getServletPath();
%>

<!-- Sidebar Overlay (mobile) -->
<div class="sidebar-overlay" id="sidebarOverlay"></div>

<!-- Top Navbar -->
<nav class="top-navbar">
    <div class="navbar-left">
        <button class="sidebar-toggle" id="sidebarToggle">
            <i class="bi bi-list"></i>
        </button>
        <h1 class="page-title"><%= request.getAttribute("pageTitle") != null ? request.getAttribute("pageTitle") : "Dashboard" %></h1>
    </div>
    <div class="navbar-right">
        <!-- Theme Toggle -->
        <button class="theme-toggle" id="themeToggle" title="Toggle Dark Mode">
            <i class="bi bi-moon-stars"></i>
        </button>

        <!-- Notifications -->
        <div style="position:relative;">
            <button class="notification-btn" id="notificationBtn" title="Notifications">
                <i class="bi bi-bell"></i>
                <% Integer unreadCount = (Integer) request.getAttribute("unreadCount"); %>
                <% if (unreadCount != null && unreadCount > 0) { %>
                    <span class="notification-badge"><%= unreadCount > 9 ? "9+" : unreadCount %></span>
                <% } %>
            </button>

            <!-- Notification Dropdown -->
            <div class="notification-dropdown" id="notificationDropdown">
                <div class="notification-header">
                    <span>Notifications</span>
                    <a href="javascript:void(0)" onclick="markAllRead()" style="font-size:12px;font-weight:500;">
                        Mark all read
                    </a>
                </div>
                <div class="notification-list" id="notificationList">
                    <div class="empty-state" style="padding:24px">
                        <p style="font-size:13px">Loading...</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- User Avatar -->
        <div class="d-flex align-items-center gap-2">
            <div style="width:36px;height:36px;border-radius:50%;background:linear-gradient(135deg,var(--primary-400),var(--accent-400));display:flex;align-items:center;justify-content:center;color:white;font-size:13px;font-weight:700;">
                <%= headerUser != null ? headerUser.getInitials() : "?" %>
            </div>
        </div>
    </div>
</nav>

<!-- Flash Messages -->
<% if (flash != null) { %>
<div class="content-area" style="padding-bottom:0;">
    <div class="flash-alert alert-<%= flash[0] %>">
        <i class="bi bi-<%= "success".equals(flash[0]) ? "check-circle-fill" : "error".equals(flash[0]) ? "exclamation-circle-fill" : "info-circle-fill" %>"></i>
        <%= flash[1] %>
    </div>
</div>
<% } %>
