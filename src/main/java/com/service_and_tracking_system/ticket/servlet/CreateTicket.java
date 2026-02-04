package com.service_and_tracking_system.ticket.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.service_and_tracking_system.ticket.dao.TicketDAO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/create-ticket")
public class CreateTicket extends HttpServlet {

    private static final Logger logger = Logger.getLogger(CreateTicket.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        logger.log(Level.INFO, "Session is null: {0}", session == null);
        if (session != null) {
            logger.log(Level.INFO, "User ID in session: {0}", session.getAttribute("userId"));
        }

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
            long ticketId = dao.createTicket(userId, categoryId, priorityId, title, description);

            response.setContentType("text/html");
            response.getWriter().println("<html><head><title>Success</title></head><body>");
            response.getWriter().println("<h3>Ticket created successfully!</h3>");
            response.getWriter().println("<p>Ticket ID: " + ticketId + "</p>");
            response.getWriter().println("<br><a href='agent-dashboard.html'>Go to Dashboard</a>");
            response.getWriter().println("</body></html>");

        } catch (Exception e) {

            logger.log(Level.SEVERE, "Error creating ticket", e);
            response.setContentType("text/html");
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h3 style='color:red;'>Ticket creation failed.</h3>");
            response.getWriter().println("<p>Please contact support if this persists.</p>");
            response.getWriter().println("</body></html>");
        }
    }
}
