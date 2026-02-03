package com.service_and_tracking_system.ticket.servlet;

import com.service_and_tracking_system.ticket.dao.TicketDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.ResultSet;

@WebServlet("/create-ticket")
public class CreateTicket extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            response.sendRedirect("login.html");
            return;
        }

        long userId = (Long) session.getAttribute("userId");

        long categoryId = Long.parseLong(request.getParameter("categoryId"));
        long priorityId = Long.parseLong(request.getParameter("priorityId"));
        String title = request.getParameter("title");
        String description = request.getParameter("description");

        try {

            TicketDAO dao = new TicketDAO();
            long ticketId =
                    dao.createTicket(userId, categoryId, priorityId, title, description);

            response.getWriter().println("Ticket created ID: " + ticketId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
