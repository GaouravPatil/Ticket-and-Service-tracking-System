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
            
            logger.log(Level.SEVERE, "An error occurred", e);
            response.setContentType("text/html");
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h3>Ticket creation failed. Please try again later.</h3>");
            response.getWriter().println("<p>Error: " + e.getMessage() + "</p>");
            response.getWriter().println("</body></html>");
        }
    }
}
