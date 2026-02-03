package com.service_and_tracking_system.ticket.servlet;

import com.service_and_tracking_system.ticket.dao.UserDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@WebServlet("/create-user")
public class CreateUserServlet extends HttpServlet {

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || !"AGENT".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {
            UserDAO dao = new UserDAO();
            String hashedPassword = hashPassword(password);
            dao.insertUser(username, email, hashedPassword);

            response.setContentType("text/html");
            response.getWriter().println("<h3>User created successfully</h3>");
            response.getWriter().println("<a href='agent-dashboard.html'>Back to Dashboard</a>");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creating user: " + e.getMessage());
        }
    }
}
