package com.service_and_tracking_system.ticket.servlet;

import com.service_and_tracking_system.ticket.util.DBConnectionUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/fetch-tickets")
public class FetchTicketsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        long userId = Long.parseLong(request.getParameter("userId"));

        String sql = """
            SELECT
              t.ticket_id,
              c.category_name,
              p.priority_name,
              s.status_name,
              t.created_at
            FROM ticket t
            JOIN category c ON t.category_id = c.category_id
            JOIN priority p ON t.priority_id = p.priority_id
            JOIN ticket_status s ON t.current_status = s.status_id
            WHERE t.user_id = ?
        """;

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h2>User Tickets</h2>");
        out.println("<table border='1'>");
        out.println("<tr>");
        out.println("<th>ID</th>");
        out.println("<th>Category</th>");
        out.println("<th>Priority</th>");
        out.println("<th>Status</th>");
        out.println("<th>Created At</th>");
        out.println("</tr>");

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getLong("ticket_id") + "</td>");
                out.println("<td>" + rs.getString("category_name") + "</td>");
                out.println("<td>" + rs.getString("priority_name") + "</td>");
                out.println("<td>" + rs.getString("status_name") + "</td>");
                out.println("<td>" + rs.getTimestamp("created_at") + "</td>");
                out.println("</tr>");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("<tr><td colspan='5'>Error fetching tickets</td></tr>");
        }

        out.println("</table>");
        out.println("</body></html>");
    }
}
