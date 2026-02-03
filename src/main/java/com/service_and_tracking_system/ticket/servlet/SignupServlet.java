package com.service_and_tracking_system.ticket.servlet;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.service_and_tracking_system.ticket.dao.UserDAO;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

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

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        try {

            UserDAO userDAO = new UserDAO();

            if (userDAO.userExists(username, email)) {
                response.sendRedirect(request.getContextPath() + "/Signup.html?error=User already exists");
                return;
            }

            String hashedPassword = hashPassword(password);
            userDAO.insertUser(username, email, hashedPassword);

            response.sendRedirect(request.getContextPath() + "/login.html");


        } catch (Exception e) {
            e.printStackTrace();

            response.sendRedirect(request.getContextPath() + "/Signup.html?error=" + e.getMessage());
        }
    }

}
