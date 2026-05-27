package com.hostelmate.controller;

import com.hostelmate.dao.RoomDAO;
import com.hostelmate.model.Room;
import com.hostelmate.util.SessionUtil;
import com.hostelmate.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AdminRoomServlet — Admin room management.
 * 
 * GET  /admin/rooms → List all rooms
 * POST /admin/rooms?action=add    → Add room
 * POST /admin/rooms?action=edit   → Edit room
 * POST /admin/rooms?action=delete → Delete room
 * 
 * @author HostelMate Team
 */
@WebServlet(name = "AdminRoomServlet", urlPatterns = {"/admin/rooms"})
public class AdminRoomServlet extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("rooms", roomDAO.getAllRooms());
        request.getRequestDispatcher("/jsp/admin/rooms.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        switch (action != null ? action : "") {
            case "add":
                handleAdd(request, response);
                break;
            case "edit":
                handleEdit(request, response);
                break;
            case "delete":
                handleDelete(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/admin/rooms");
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String roomNumber = ValidationUtil.clean(request.getParameter("roomNumber"));
        int floor    = ValidationUtil.parseIntSafe(request.getParameter("floor"), 1);
        int capacity = ValidationUtil.parseIntSafe(request.getParameter("capacity"), 4);

        if (ValidationUtil.isEmpty(roomNumber)) {
            SessionUtil.setFlashMessage(request, "error", "Room number is required.");
        } else if (roomDAO.roomExists(roomNumber)) {
            SessionUtil.setFlashMessage(request, "error", "Room number already exists.");
        } else {
            Room room = new Room(roomNumber, floor, capacity);
            if (roomDAO.addRoom(room)) {
                SessionUtil.setFlashMessage(request, "success", "Room added successfully!");
            } else {
                SessionUtil.setFlashMessage(request, "error", "Failed to add room.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/rooms");
    }

    private void handleEdit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int roomId = ValidationUtil.parseIntSafe(request.getParameter("roomId"), 0);
        Room room = roomDAO.findById(roomId);

        if (room == null) {
            SessionUtil.setFlashMessage(request, "error", "Room not found.");
        } else {
            room.setRoomNumber(ValidationUtil.clean(request.getParameter("roomNumber")));
            room.setFloor(ValidationUtil.parseIntSafe(request.getParameter("floor"), room.getFloor()));
            room.setCapacity(ValidationUtil.parseIntSafe(request.getParameter("capacity"), room.getCapacity()));

            if (roomDAO.updateRoom(room)) {
                SessionUtil.setFlashMessage(request, "success", "Room updated successfully!");
            } else {
                SessionUtil.setFlashMessage(request, "error", "Failed to update room.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/rooms");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int roomId = ValidationUtil.parseIntSafe(request.getParameter("roomId"), 0);

        if (roomDAO.deleteRoom(roomId)) {
            SessionUtil.setFlashMessage(request, "success", "Room deleted successfully!");
        } else {
            SessionUtil.setFlashMessage(request, "error", 
                "Failed to delete room. Make sure no residents are assigned to it.");
        }
        response.sendRedirect(request.getContextPath() + "/admin/rooms");
    }
}
