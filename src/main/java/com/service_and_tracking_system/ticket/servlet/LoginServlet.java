package com.service_and_tracking_system.ticket.servlet;

import com.service_and_tracking_system.ticket.dao.UserDAO;
import com.service_and_tracking_system.ticket.util.DBConnectionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String username = request.getParameter("username");



        ResultSet rs = null;
        try (Connection con = DBConnectionUtil.getConnection()) {

            UserDAO dao = new UserDAO();
            rs = dao.findByEmail(con, email);
            dao.insertUser(username, email,password);

            if (rs.next()) {

                String dbPassword = rs.getString("password_hash");


                if (password.equals(dbPassword)) {

                    HttpSession session = request.getSession();
                    session.setAttribute("userId", rs.getLong("user_id"));
                    session.setAttribute("username", rs.getString("username"));
                    session.setAttribute("role", rs.getString("role"));

                    response.sendRedirect("create-ticket.html");
                    return;
                }
            }

            response.getWriter().println("Invalid login");

            String role = rs.getString("role");

            HttpSession session = request.getSession();
            session.setAttribute("userId", rs.getLong("user_id"));
            session.setAttribute("role", role);

            if ("AGENT".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/agent-dashboard.html");
            } else {
                response.sendRedirect(request.getContextPath() + "/create-ticket.html");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
