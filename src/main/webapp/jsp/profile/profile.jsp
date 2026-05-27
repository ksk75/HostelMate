<%-- HostelMate — Profile Page --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hostelmate.model.*" %>
<%@ page import="com.hostelmate.util.SessionUtil" %>
<%@ page import="java.util.List" %>
<% request.setAttribute("pageTitle", "Profile"); 
   User profUser = (User) request.getAttribute("profileUser");
   List<Room> profRooms = (List<Room>) request.getAttribute("rooms");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profile — HostelMate</title>
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
                <!-- Profile Card -->
                <div class="col-lg-4">
                    <div class="content-card" style="text-align:center;">
                        <div class="card-body" style="padding:40px 24px;">
                            <div style="width:80px;height:80px;border-radius:50%;background:linear-gradient(135deg,var(--primary-400),var(--accent-400));display:inline-flex;align-items:center;justify-content:center;color:white;font-size:28px;font-weight:800;margin-bottom:16px;">
                                <%= profUser.getInitials() %>
                            </div>
                            <h4 style="font-weight:800;margin-bottom:4px;"><%= profUser.getFullName() %></h4>
                            <p style="color:var(--text-muted);font-size:14px;"><%= profUser.getEmail() %></p>
                            <span class="badge-status badge-<%= profUser.getRole().toLowerCase() %>"><%= profUser.getRole() %></span>
                            <div style="margin-top:20px;padding-top:20px;border-top:1px solid var(--border-color);text-align:left;">
                                <p style="font-size:13px;margin-bottom:8px;"><i class="bi bi-phone me-2"></i><strong>Phone:</strong> <%= profUser.getPhone() != null ? profUser.getPhone() : "Not set" %></p>
                                <p style="font-size:13px;margin-bottom:8px;"><i class="bi bi-door-open me-2"></i><strong>Room:</strong> <%= profUser.getRoomNumber() != null ? "Room " + profUser.getRoomNumber() : "Not assigned" %></p>
                                <p style="font-size:13px;margin-bottom:0;"><i class="bi bi-calendar3 me-2"></i><strong>Joined:</strong> <%= profUser.getCreatedAt() != null ? new java.text.SimpleDateFormat("dd MMM yyyy").format(profUser.getCreatedAt()) : "" %></p>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-lg-8">
                    <!-- Update Profile -->
                    <div class="content-card">
                        <div class="card-header"><h3><i class="bi bi-pencil-square me-2"></i>Update Profile</h3></div>
                        <div class="card-body">
                            <form method="POST" action="<%= request.getContextPath() %>/student/profile">
                                <input type="hidden" name="action" value="update">
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label class="form-label">Full Name</label>
                                        <input type="text" class="form-control" name="fullName" value="<%= profUser.getFullName() %>" required>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Phone</label>
                                        <input type="tel" class="form-control" name="phone" value="<%= profUser.getPhone() != null ? profUser.getPhone() : "" %>" pattern="[6-9]\d{9}">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Room</label>
                                        <select class="form-select" name="roomId">
                                            <option value="0">-- No Room --</option>
                                            <% if (profRooms != null) for (Room r : profRooms) { %>
                                                <option value="<%= r.getId() %>" <%= r.getId() == profUser.getRoomId() ? "selected" : "" %>>Room <%= r.getRoomNumber() %></option>
                                            <% } %>
                                        </select>
                                    </div>
                                    <div class="col-12">
                                        <button type="submit" class="btn-primary-gradient"><i class="bi bi-check-lg"></i> Save Changes</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>

                    <!-- Change Password -->
                    <div class="content-card">
                        <div class="card-header"><h3><i class="bi bi-shield-lock me-2"></i>Change Password</h3></div>
                        <div class="card-body">
                            <form method="POST" action="<%= request.getContextPath() %>/student/profile">
                                <input type="hidden" name="action" value="password">
                                <div class="row g-3">
                                    <div class="col-md-4">
                                        <label class="form-label">Current Password</label>
                                        <input type="password" class="form-control" name="currentPassword" required>
                                    </div>
                                    <div class="col-md-4">
                                        <label class="form-label">New Password</label>
                                        <input type="password" class="form-control" name="newPassword" required minlength="6">
                                    </div>
                                    <div class="col-md-4">
                                        <label class="form-label">Confirm New Password</label>
                                        <input type="password" class="form-control" name="confirmPassword" required minlength="6">
                                    </div>
                                    <div class="col-12">
                                        <button type="submit" class="btn-primary-gradient"><i class="bi bi-key"></i> Change Password</button>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%@ include file="/jsp/common/footer.jsp" %>
