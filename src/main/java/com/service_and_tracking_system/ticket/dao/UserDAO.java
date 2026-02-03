package com.service_and_tracking_system.ticket.dao;

import com.service_and_tracking_system.ticket.util.DBConnectionUtil;

import java.sql.*;

public class UserDAO {

    public boolean userExists(String username, String email) throws Exception {

        String sql = "SELECT 1 FROM users WHERE username=? OR email=?";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, email);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public void insertUser(String username, String email, String password) throws Exception {

        String sql =
                "INSERT INTO users (username,email,password_hash,role,is_active) " +
                        "VALUES (?,?,?,?,TRUE)";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, "USER");


            ps.executeUpdate();
        }
    }

    public ResultSet loginUser(Connection con, String email) throws Exception {

        String sql =
                "SELECT user_id,username,role,password_hash FROM users WHERE email=?";

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, email);

        return ps.executeQuery();
    }

    public ResultSet findByEmail(Connection con, String email) throws Exception {

            String sql =
                    "SELECT user_id,username,role,password_hash FROM users WHERE email=? AND is_active=TRUE";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);

            return ps.executeQuery();
        }




}

