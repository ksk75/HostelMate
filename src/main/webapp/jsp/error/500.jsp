<%-- HostelMate — 500 Error Page --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Server Error — HostelMate</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/auth.css" rel="stylesheet">
</head>
<body>
<div class="auth-page">
    <div class="auth-container" style="text-align:center">
        <div class="auth-card">
            <div style="font-size:80px;color:var(--danger);margin-bottom:16px">
                <i class="bi bi-exclamation-triangle"></i>
            </div>
            <h1 style="font-size:48px;font-weight:900;color:var(--text-primary);margin-bottom:8px">500</h1>
            <p style="color:var(--text-secondary);font-size:16px;margin-bottom:24px">
                Oops! Something went wrong on our end. Please try again later.
            </p>
            <a href="<%= request.getContextPath() %>/" class="btn-primary-gradient" style="display:inline-flex;gap:8px;padding:14px 32px;border-radius:10px;text-decoration:none">
                <i class="bi bi-house"></i> Back to Home
            </a>
        </div>
    </div>
</div>
</body>
</html>
