<%-- 
    HostelMate — Login Page 
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hostelmate.util.SessionUtil" %>
<%
    String[] loginFlash = SessionUtil.getFlashMessage(request);
    String loginError = (String) request.getAttribute("error");
    String loginEmail = (String) request.getAttribute("email");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login — HostelMate</title>
    <meta name="description" content="Log in to HostelMate to manage your hostel expenses.">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/auth.css" rel="stylesheet">
</head>
<body>
<div class="auth-page">
    <div class="auth-container">
        <div class="auth-card">
            <!-- Brand -->
            <div class="auth-brand">
                <div class="brand-logo"><i class="bi bi-house-heart"></i></div>
                <h1>Welcome <span>Back</span></h1>
                <p>Log in to manage your hostel expenses</p>
            </div>

            <!-- Flash Message -->
            <% if (loginFlash != null) { %>
                <div class="auth-alert <%= "success".equals(loginFlash[0]) ? "success" : "error" %>">
                    <i class="bi bi-<%= "success".equals(loginFlash[0]) ? "check-circle" : "exclamation-circle" %>"></i>
                    <%= loginFlash[1] %>
                </div>
            <% } %>

            <!-- Error Message -->
            <% if (loginError != null) { %>
                <div class="auth-alert error">
                    <i class="bi bi-exclamation-circle"></i>
                    <%= loginError %>
                </div>
            <% } %>

            <!-- Login Form -->
            <form class="auth-form" method="POST" action="<%= request.getContextPath() %>/login" id="loginForm">
                <div class="form-group">
                    <label class="form-label" for="email">Email Address</label>
                    <div class="input-group">
                        <i class="bi bi-envelope input-icon"></i>
                        <input type="email" class="form-control" id="email" name="email" 
                               placeholder="you@hostelmate.com" required
                               value="<%= loginEmail != null ? loginEmail : "" %>">
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">Password</label>
                    <div class="input-group">
                        <i class="bi bi-lock input-icon"></i>
                        <input type="password" class="form-control" id="password" name="password" 
                               placeholder="Enter your password" required minlength="6">
                        <button type="button" class="toggle-password" onclick="togglePassword('password', this)">
                            <i class="bi bi-eye"></i>
                        </button>
                    </div>
                </div>

                <button type="submit" class="btn-auth">
                    <i class="bi bi-box-arrow-in-right"></i> Log In
                </button>
            </form>

            <!-- Links -->
            <div class="auth-links">
                Don't have an account? <a href="<%= request.getContextPath() %>/register">Sign Up</a>
            </div>
        </div>
    </div>
</div>

<script>
function togglePassword(fieldId, btn) {
    const field = document.getElementById(fieldId);
    const icon = btn.querySelector('i');
    if (field.type === 'password') {
        field.type = 'text';
        icon.className = 'bi bi-eye-slash';
    } else {
        field.type = 'password';
        icon.className = 'bi bi-eye';
    }
}
</script>
<script src="<%= request.getContextPath() %>/js/app.js"></script>
</body>
</html>
