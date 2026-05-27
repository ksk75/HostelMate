<%-- HostelMate — Admin Rooms Page --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.hostelmate.model.*" %>
<%@ page import="java.util.List" %>
<% request.setAttribute("pageTitle", "Manage Rooms");
   List<Room> rooms = (List<Room>) request.getAttribute("rooms");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Rooms — HostelMate</title>
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
                <h3 style="font-weight:700"><%= rooms != null ? rooms.size() : 0 %> Rooms</h3>
                <button class="btn-primary-gradient" data-bs-toggle="modal" data-bs-target="#addRoomModal"><i class="bi bi-plus-lg"></i> Add Room</button>
            </div>
            <div class="row g-4">
                <% if (rooms != null) for (Room r : rooms) { %>
                <div class="col-md-6 col-lg-4 col-xl-3">
                    <div class="stat-card card-accent" style="padding:20px">
                        <div class="d-flex justify-content-between align-items-start mb-3">
                            <div>
                                <div class="card-label">Room</div>
                                <div style="font-size:24px;font-weight:800;color:var(--text-primary)"><%= r.getRoomNumber() %></div>
                            </div>
                            <div class="card-icon icon-accent" style="width:40px;height:40px;font-size:18px"><i class="bi bi-door-open"></i></div>
                        </div>
                        <div class="d-flex gap-3 mb-3" style="font-size:13px;color:var(--text-secondary)">
                            <span><i class="bi bi-layers me-1"></i>Floor <%= r.getFloor() %></span>
                            <span><i class="bi bi-people me-1"></i><%= r.getOccupantCount() %>/<%= r.getCapacity() %></span>
                        </div>
                        <div style="background:var(--gray-100);border-radius:8px;height:6px;overflow:hidden">
                            <div style="background:linear-gradient(90deg,var(--accent-500),var(--accent-400));height:100%;width:<%= r.getCapacity() > 0 ? (r.getOccupantCount() * 100 / r.getCapacity()) : 0 %>%;border-radius:8px;transition:width 500ms ease"></div>
                        </div>
                        <div class="d-flex gap-2 mt-3">
                            <form method="POST" action="<%= request.getContextPath() %>/admin/rooms" style="display:inline" onsubmit="return confirm('Delete room <%= r.getRoomNumber() %>?')">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="roomId" value="<%= r.getId() %>">
                                <button type="submit" class="btn-sm-action btn-danger" <%= r.getOccupantCount() > 0 ? "disabled title='Has occupants'" : "" %>><i class="bi bi-trash"></i> Delete</button>
                            </form>
                        </div>
                    </div>
                </div>
                <% } %>
            </div>
        </div>
    </div>
    <!-- Add Room Modal -->
    <div class="modal fade" id="addRoomModal" tabindex="-1"><div class="modal-dialog"><div class="modal-content">
        <div class="modal-header"><h5 class="modal-title" style="font-weight:700"><i class="bi bi-door-open me-2"></i>Add Room</h5><button type="button" class="btn-close" data-bs-dismiss="modal"></button></div>
        <form method="POST" action="<%= request.getContextPath() %>/admin/rooms"><input type="hidden" name="action" value="add">
            <div class="modal-body"><div class="row g-3">
                <div class="col-12"><label class="form-label">Room Number *</label><input type="text" class="form-control" name="roomNumber" required placeholder="e.g., A-101"></div>
                <div class="col-md-6"><label class="form-label">Floor</label><input type="number" class="form-control" name="floor" min="0" max="20" value="1"></div>
                <div class="col-md-6"><label class="form-label">Capacity</label><input type="number" class="form-control" name="capacity" min="1" max="10" value="4"></div>
            </div></div>
            <div class="modal-footer"><button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button><button type="submit" class="btn-primary-gradient"><i class="bi bi-check-lg"></i> Add Room</button></div>
        </form>
    </div></div></div>
    <%@ include file="/jsp/common/footer.jsp" %>
