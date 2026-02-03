package com.service_and_tracking_system.ticket.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import com.service_and_tracking_system.ticket.util.DBConnectionUtil;

public class TicketDAO {

    private static final Logger logger = Logger.getLogger(TicketDAO.class.getName());

    public long createTicket(long userId,
            long categoryId,
            long priorityId,
            String title,
            String description) throws Exception {

        String insertTicketSql = "INSERT INTO ticket (user_id,category_id,priority_id,title,description,current_status) "
                +
                "VALUES (?,?,?,?,?,'OPEN')";

        String historySql = "INSERT INTO ticket_status_history (ticket_id,status_id,updated_by,remarks) VALUES (?,?,?,?)";

        Connection con = null;
        try {
            con = DBConnectionUtil.getConnection();
            con.setAutoCommit(false);

            long ticketId;

            try (PreparedStatement ps = con.prepareStatement(insertTicketSql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setLong(1, userId);
                ps.setLong(2, categoryId);
                ps.setLong(3, priorityId);
                ps.setString(4, title);
                ps.setString(5, description);

                int rowsAffected = ps.executeUpdate();
                logger.info("Rows affected in ticket table: " + rowsAffected);

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    ticketId = rs.getLong(1);
                    logger.info("Generated ticket ID: " + ticketId);
                } else {
                    throw new SQLException("Failed to retrieve ticket ID.");
                }
            }

            long openStatusId = -1;

            // Check if OPEN status exists
            try (PreparedStatement ps = con.prepareStatement(
                    "SELECT status_id FROM ticket_status WHERE status_name='OPEN'")) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        openStatusId = rs.getLong(1);
                        logger.info("Found existing Open status ID: " + openStatusId);
                    }
                }
            }

            // If not found, create it
            if (openStatusId == -1) {
                logger.warning("'OPEN' status not found in ticket_status. Attempting to create it.");
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO ticket_status (status_name) VALUES ('OPEN')", Statement.RETURN_GENERATED_KEYS)) {
                    ps.executeUpdate();
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        openStatusId = rs.getLong(1);
                        logger.info("Created new Open status ID: " + openStatusId);
                    } else {
                        // Some databases might not return keys for simple inserts or if not requested
                        // properly,
                        // strictly speaking we should have exception here, but let's try selecting
                        // again or fail
                        throw new SQLException("Failed to create 'OPEN' status in ticket_status table.");
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(historySql)) {
                ps.setLong(1, ticketId);
                ps.setLong(2, openStatusId);
                ps.setLong(3, userId);
                ps.setString(4, "Ticket created");

                int historyRowsAffected = ps.executeUpdate();
                logger.info("Rows affected in ticket_status_history table: " + historyRowsAffected);
            }

            con.commit();
            return ticketId;

        } catch (Exception e) {
            if (con != null) {
                try {
                    logger.severe("Rolling back transaction due to error: " + e.getMessage());
                    con.rollback();
                } catch (SQLException ex) {
                    logger.severe("Error during rollback: " + ex.getMessage());
                }
            }
            logger.severe("Error creating ticket: " + e.getMessage());
            throw e;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.warning("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    public ResultSet getTicketDetails(Connection con, long ticketId) throws Exception {

        String sql = """
                    SELECT ts.status_name, h.updated_by, h.remarks, h.updated_at
                    FROM ticket_status_history h
                    JOIN ticket_status ts ON h.status_id = ts.status_id
                    WHERE h.ticket_id = ?
                    ORDER BY h.updated_at ASC
                """;

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setLong(1, ticketId);

        return ps.executeQuery();
    }

    public ResultSet getTicketHistory(Connection con, long ticketId) throws Exception {

        String sql = """
                    SELECT ts.status_name, h.updated_by, h.remarks, h.updated_at
                    FROM ticket_status_history h
                    JOIN ticket_status ts ON h.status_id = ts.status_id
                    WHERE h.ticket_id = ?
                    ORDER BY h.updated_at ASC
                """;

        PreparedStatement ps = con.prepareStatement(sql);
        ps.setLong(1, ticketId);

        return ps.executeQuery();
    }

}
