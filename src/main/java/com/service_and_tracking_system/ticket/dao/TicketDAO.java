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

        String insertTicketSql =
                "INSERT INTO ticket (user_id,category_id,priority_id,title,description,current_status) " +
                        "VALUES (?,?,?,?,?,'OPEN')";

        String historySql =
                "INSERT INTO ticket_status_history (ticket_id,status_id,updated_by,remarks) VALUES (?,?,?,?)";

        try (Connection con = DBConnectionUtil.getConnection()) {

            con.setAutoCommit(false);

            long ticketId;

            try (PreparedStatement ps =
                         con.prepareStatement(insertTicketSql, Statement.RETURN_GENERATED_KEYS)) {

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

            long openStatusId;

            try (PreparedStatement ps =
                         con.prepareStatement(
                                 "SELECT status_id FROM ticket_status WHERE status_name='OPEN'");
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    openStatusId = rs.getLong(1);
                    logger.info("Open status ID: " + openStatusId);
                } else {
                    throw new SQLException("Open status ID not found in ticket_status table.");
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
            logger.severe("Error creating ticket: " + e.getMessage());
            throw e;
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
