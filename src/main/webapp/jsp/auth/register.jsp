<%-- HostelMate — Registration Page --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hostelmate.model.Room" %>
<%@ page import="java.util.List" %>
<%
    String regError = (String) request.getAttribute("error");
    String regName  = (String) request.getAttribute("fullName");
    String regEmail = (String) request.getAttribute("email");
    String regPhone = (String) request.getAttribute("phone");
    List<Room> regRooms = (List<Room>) request.getAttribute("rooms");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register — HostelMate</title>
    <meta name="description" content="Create your HostelMate account to start managing hostel expenses.">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/style.css" rel="stylesheet">
    <link href="<%= request.getContextPath() %>/css/auth.css" rel="stylesheet">
</head>
<body>
<div class="auth-page">
    <div class="auth-container" style="max-width:480px">
        <div class="auth-card">
            <div class="auth-brand">
                <div class="brand-logo"><i class="bi bi-house-heart"></i></div>
                <h1>Create <span>Account</span></h1>
                <p>Join HostelMate to manage shared expenses</p>
            </div>

            <% if (regError != null) { %>
                <div class="auth-alert error">
                    <i class="bi bi-exclamation-circle"></i> <%= regError %>
                </div>
            <% } %>

            <form class="auth-form" method="POST" action="<%= request.getContextPath() %>/register" id="registerForm">
                <div class="form-group">
                    <label class="form-label" for="fullName">Full Name</label>
                    <div class="input-group">
                        <i class="bi bi-person input-icon"></i>
                        <input type="text" class="form-control" id="fullName" name="fullName" 
                               placeholder="Enter your full name" required
                               value="<%= regName != null ? regName : "" %>">
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="email">Email Address</label>
                    <div class="input-group">
                        <i class="bi bi-envelope input-icon"></i>
                        <input type="email" class="form-control" id="email" name="email" 
                               placeholder="you@hostelmate.com" required
                               value="<%= regEmail != null ? regEmail : "" %>">
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="phone">Phone Number (Optional)</label>
                    <div class="input-group">
                        <i class="bi bi-phone input-icon"></i>
                        <input type="tel" class="form-control" id="phone" name="phone" 
                               placeholder="10-digit mobile number" pattern="[6-9]\d{9}"
                               value="<%= regPhone != null ? regPhone : "" %>">
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="roomId">Room (Optional)</label>
                    <select class="form-control" id="roomId" name="roomId" style="padding-left:16px">
                        <option value="0">-- Select Room --</option>
                        <% if (regRooms != null) { for (Room r : regRooms) { %>
                            <option value="<%= r.getId() %>">
                                Room <%= r.getRoomNumber() %> (Floor <%= r.getFloor() %>, 
                                <%= r.getAvailableBeds() %>/<%= r.getCapacity() %> beds free)
                            </option>
                        <% } } %>
                    </select>
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">Password</label>
                    <div class="input-group">
                        <i class="bi bi-lock input-icon"></i>
                        <input type="password" class="form-control" id="password" name="password" 
                               placeholder="At least 6 characters" required minlength="6">
                        <button type="button" class="toggle-password" onclick="togglePassword('password', this)">
                            <i class="bi bi-eye"></i>
                        </button>
                    </div>
                </div>

                <div class="form-group">
                    <label class="form-label" for="confirmPassword">Confirm Password</label>
                    <div class="input-group">
                        <i class="bi bi-lock input-icon"></i>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" 
                               placeholder="Re-enter password" required minlength="6">
                    </div>
                </div>

                <button type="submit" class="btn-auth">
                    <i class="bi bi-person-plus"></i> Create Account
                </button>
            </form>

            <div class="auth-links">
                Already have an account? <a href="<%= request.getContextPath() %>/login">Log In</a>
            </div>
        </div>
    </div>
</div>

<script>
function togglePassword(fieldId, btn) {
    const field = document.getElementById(fieldId);
    const icon = btn.querySelector('i');
    field.type = field.type === 'password' ? 'text' : 'password';
    icon.className = field.type === 'password' ? 'bi bi-eye' : 'bi bi-eye-slash';
}
</script>
<script src="<%= request.getContextPath() %>/js/app.js"></script>
</body>
</html>
