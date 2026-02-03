package com.service_and_tracking_system.ticket.servlet;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.service_and_tracking_system.ticket.dao.UserDAO;
import com.service_and_tracking_system.ticket.util.DBConnectionUtil;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());

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

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try (Connection con = DBConnectionUtil.getConnection()) {

            UserDAO dao = new UserDAO();
            try (ResultSet rs = dao.findByEmail(con, email)) {

                if (rs.next()) {
                    String dbPasswordHash = rs.getString("password_hash");
                    String inputPasswordHash = hashPassword(password);

                    if (inputPasswordHash.equals(dbPasswordHash)) {
                        HttpSession session = request.getSession();
                        session.setAttribute("userId", rs.getLong("user_id"));
                        session.setAttribute("username", rs.getString("username"));
                        String role = rs.getString("role");
                        session.setAttribute("role", role);

                        logger.info("User role retrieved: " + role);

                        if ("AGENT".equalsIgnoreCase(role)) {
                            response.sendRedirect(request.getContextPath() + "/agent-dashboard.html");
                        } else {
                            response.sendRedirect(request.getContextPath() + "/create-ticket.html");
                        }
                        return;
                    } else {
                        logger.warning("Invalid password attempt for email: " + email);
                    }
                } else {
                    logger.warning("User not found for email: " + email);
                }
            }

            // If we reach here, login failed
            response.setContentType("text/html");
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h3 style='color:red;'>Invalid login credentials</h3>");
            response.getWriter().println("<a href='login.html'>Try Again</a>");
            response.getWriter().println("</body></html>");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Login error", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred during login.");
        }
    }
}
