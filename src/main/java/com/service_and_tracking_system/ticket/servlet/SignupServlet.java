package com.service_and_tracking_system.ticket.servlet;

import com.service_and_tracking_system.ticket.dao.UserDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {

            UserDAO userDAO = new UserDAO();

            if (userDAO.userExists(username, email)) {
                response.getWriter().println("User already exists");
                return;
            }

            userDAO.insertUser(username, email, password);

            //REDIRECT
            response.sendRedirect(request.getContextPath() + "/login.html");


        } catch (Exception e) {
            e.printStackTrace();

            response.getWriter().println(e.getMessage());
        }
    }

}
