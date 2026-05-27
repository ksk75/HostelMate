<%-- HostelMate — Admin Users Page --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hostelmate.model.*" %>
<%@ page import="java.util.List" %>
<% request.setAttribute("pageTitle", "Manage Users");
   List<User> users = (List<User>) request.getAttribute("users");
   List<Room> rooms = (List<Room>) request.getAttribute("rooms");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Users — HostelMate</title>
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
            <div class="d-flex justify-content-between align-items-center mb-3">
                <h3 style="font-weight:700"><%= users != null ? users.size() : 0 %> Users</h3>
                <button class="btn-primary-gradient" data-bs-toggle="modal" data-bs-target="#addUserModal"><i class="bi bi-person-plus"></i> Add User</button>
            </div>
            <div class="content-card"><div class="table-responsive"><table class="data-table">
                <thead><tr><th>User</th><th>Email</th><th>Phone</th><th>Room</th><th>Role</th><th>Status</th><th>Actions</th></tr></thead>
                <tbody>
                <% if (users != null) for (User u : users) { %>
                <tr>
                    <td><div class="d-flex align-items-center gap-2">
                        <div style="width:32px;height:32px;border-radius:50%;background:linear-gradient(135deg,var(--primary-400),var(--accent-400));display:flex;align-items:center;justify-content:center;color:white;font-size:11px;font-weight:700"><%= u.getInitials() %></div>
                        <strong><%= u.getFullName() %></strong>
                    </div></td>
                    <td><%= u.getEmail() %></td>
                    <td><%= u.getPhone() != null ? u.getPhone() : "-" %></td>
                    <td><%= u.getRoomNumber() != null ? "Room " + u.getRoomNumber() : "-" %></td>
                    <td><span class="badge-status badge-<%= u.getRole().toLowerCase() %>"><%= u.getRole() %></span></td>
                    <td><span class="badge-status <%= u.isActive() ? "badge-active" : "badge-blocked" %>"><%= u.isActive() ? "Active" : "Blocked" %></span></td>
                    <td>
                        <% if (!u.isAdmin()) { %>
                        <form method="POST" action="<%= request.getContextPath() %>/admin/users" style="display:inline">
                            <input type="hidden" name="action" value="block">
                            <input type="hidden" name="userId" value="<%= u.getId() %>">
                            <input type="hidden" name="active" value="<%= !u.isActive() %>">
                            <button type="submit" class="btn-sm-action" title="<%= u.isActive() ? "Block" : "Unblock" %>">
                                <i class="bi bi-<%= u.isActive() ? "slash-circle" : "check-circle" %>"></i>
                            </button>
                        </form>
                        <form method="POST" action="<%= request.getContextPath() %>/admin/users" style="display:inline" onsubmit="return confirm('Delete user <%= u.getFullName() %>?')">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="userId" value="<%= u.getId() %>">
                            <button type="submit" class="btn-sm-action btn-danger"><i class="bi bi-trash"></i></button>
                        </form>
                        <% } %>
                    </td>
                </tr>
                <% } %>
                </tbody></table></div></div>
        </div>
    </div>
    <!-- Add User Modal -->
    <div class="modal fade" id="addUserModal" tabindex="-1"><div class="modal-dialog"><div class="modal-content">
        <div class="modal-header"><h5 class="modal-title" style="font-weight:700"><i class="bi bi-person-plus me-2"></i>Add User</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
        <form method="POST" action="<%= request.getContextPath() %>/admin/users"><input type="hidden" name="action" value="add">
            <div class="modal-body"><div class="row g-3">
                <div class="col-12"><label class="form-label">Full Name *</label><input type="text" class="form-control" name="fullName" required></div>
                <div class="col-12"><label class="form-label">Email *</label><input type="email" class="form-control" name="email" required></div>
                <div class="col-md-6"><label class="form-label">Password *</label><input type="password" class="form-control" name="password" required minlength="6"></div>
                <div class="col-md-6"><label class="form-label">Phone</label><input type="tel" class="form-control" name="phone"></div>
                <div class="col-md-6"><label class="form-label">Role</label><select class="form-select" name="role"><option value="STUDENT">Student</option><option value="ADMIN">Admin</option></select></div>
                <div class="col-md-6"><label class="form-label">Room</label><select class="form-select" name="roomId"><option value="0">-- None --</option>
                    <% if (rooms != null) for (Room r : rooms) { %><option value="<%= r.getId() %>">Room <%= r.getRoomNumber() %></option><% } %>
                </select></div>
            </div></div>
            <div class="modal-footer"><button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button><button type="submit" class="btn-primary-gradient"><i class="bi bi-check-lg"></i> Add User</button></div>
        </form>
    </div></div></div>
    <%@ include file="/jsp/common/footer.jsp" %>
