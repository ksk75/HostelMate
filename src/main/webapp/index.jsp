<%-- 
    HostelMate — Landing Page (index.jsp)
    Public-facing page with hero section and feature highlights.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HostelMate — Hostel Expense Management System</title>
    <meta name="description" content="HostelMate helps hostel residents manage shared expenses, track payments, and settle balances with roommates easily.">
    
    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800;900&display=swap" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <!-- CSS -->
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/landing.css" rel="stylesheet">
</head>
<body>
<div class="landing-page">

    <!-- Navigation -->
    <nav class="landing-nav" id="landingNav">
        <div class="nav-brand">
            <i class="bi bi-house-heart"></i>
            Hostel<span style="color:#2dd4bf">Mate</span>
        </div>
        <div class="nav-actions">
            <a href="<%= request.getContextPath() %>/login" class="btn-nav btn-nav-outline">Log In</a>
            <a href="<%= request.getContextPath() %>/register" class="btn-nav btn-nav-primary">Get Started</a>
        </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero-section">
        <div class="shape shape-1"></div>
        <div class="shape shape-2"></div>
        <div class="shape shape-3"></div>
        
        <div class="hero-content">
            <div class="hero-badge">
                <i class="bi bi-lightning-fill"></i> Smart Expense Management
            </div>
            <h1>
                Split Expenses,<br>
                <span class="gradient-text">Not Friendships</span>
            </h1>
            <p>
                HostelMate helps hostel residents track shared expenses, split bills fairly, 
                settle balances instantly, and maintain transparency — all in one place.
            </p>
            <div class="hero-buttons">
                <a href="<%= request.getContextPath() %>/register" class="btn-hero btn-hero-primary">
                    <i class="bi bi-rocket-takeoff"></i> Get Started Free
                </a>
                <a href="#features" class="btn-hero btn-hero-outline">
                    <i class="bi bi-play-circle"></i> Explore Features
                </a>
            </div>
        </div>
    </section>

    <!-- Features Section -->
    <section class="features-section" id="features">
        <div class="section-header">
            <div class="section-label">
                <i class="bi bi-stars"></i> Features
            </div>
            <h2>Everything You Need</h2>
            <p>From splitting WiFi bills to tracking monthly rent — HostelMate handles it all.</p>
        </div>

        <div class="features-grid">
            <div class="feature-card">
                <div class="feature-icon fi-1"><i class="bi bi-calculator"></i></div>
                <h3>Smart Splitting</h3>
                <p>Split expenses equally or custom amounts. The system automatically calculates who owes whom.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon fi-2"><i class="bi bi-graph-up-arrow"></i></div>
                <h3>Analytics & Reports</h3>
                <p>Visual charts show monthly trends, category breakdowns, and spending patterns.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon fi-3"><i class="bi bi-bell"></i></div>
                <h3>Smart Notifications</h3>
                <p>Get alerts for pending payments, new expenses, and monthly rent reminders.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon fi-4"><i class="bi bi-shield-check"></i></div>
                <h3>Secure & Private</h3>
                <p>BCrypt password hashing, SQL injection prevention, and session-based authentication.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon fi-5"><i class="bi bi-tags"></i></div>
                <h3>Categorize Expenses</h3>
                <p>Organize expenses into Rent, Food, Electricity, WiFi, Maintenance, and more.</p>
            </div>
            <div class="feature-card">
                <div class="feature-icon fi-6"><i class="bi bi-arrow-left-right"></i></div>
                <h3>Balance Settlement</h3>
                <p>Track and settle balances between roommates with a simplified debt algorithm.</p>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="landing-footer">
        <p>&copy; 2026 HostelMate — Hostel Expense Management System. Built for MCA Mini Project.</p>
    </footer>
</div>

<!-- JS -->
<script src="<%= request.getContextPath() %>/js/app.js"></script>
</body>
</html>
