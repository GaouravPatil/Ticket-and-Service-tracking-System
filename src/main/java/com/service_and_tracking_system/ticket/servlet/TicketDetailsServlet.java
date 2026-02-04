package com.service_and_tracking_system.ticket.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.service_and_tracking_system.ticket.dao.TicketDAO;
import com.service_and_tracking_system.ticket.util.DBConnectionUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ticket-details")
public class TicketDetailsServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(TicketDetailsServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        long ticketId =
                Long.parseLong(request.getParameter("ticketId"));

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection con = DBConnectionUtil.getConnection()) {

            TicketDAO dao = new TicketDAO();

            ResultSet tRs = dao.getTicketDetails(con, ticketId);

            if (!tRs.next()) {
                out.println("<h3>Ticket not found</h3>");
                return;
            }

            out.println("<h2>Ticket Details</h2>");
            out.println("<p>ID: " + tRs.getLong("ticket_id") + "</p>");
            out.println("<p>Title: " + tRs.getString("title") + "</p>");
            out.println("<p>Description: " + tRs.getString("description") + "</p>");
            out.println("<p>Category: " + tRs.getString("category_name") + "</p>");
            out.println("<p>Priority: " + tRs.getString("priority_name") + "</p>");
            out.println("<p>Status: " + tRs.getString("status_name") + "</p>");
            out.println("<p>Created: " + tRs.getTimestamp("created_at") + "</p>");

            ResultSet hRs = dao.getTicketHistory(con, ticketId);

            out.println("<h3>Status History</h3>");
            out.println("<table border='1'>");
            out.println("<tr><th>Status</th><th>Updated By</th><th>Remarks</th><th>Time</th></tr>");

            while (hRs.next()) {
                out.println("<tr>");
                out.println("<td>" + hRs.getString("status_name") + "</td>");
                out.println("<td>" + hRs.getLong("updated_by") + "</td>");
                out.println("<td>" + hRs.getString("remarks") + "</td>");
                out.println("<td>" + hRs.getTimestamp("updated_at") + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading ticket details", e);
            out.println("<h3>Error loading ticket details</h3>");
        }
    }
}

