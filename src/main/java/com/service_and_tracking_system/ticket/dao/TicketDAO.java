package com.service_and_tracking_system.ticket.dao;

import com.service_and_tracking_system.ticket.util.DBConnectionUtil;

import java.sql.*;

public class TicketDAO {

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

                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                rs.next();
                ticketId = rs.getLong(1);
            }

            long openStatusId;

            try (PreparedStatement ps =
                         con.prepareStatement(
                                 "SELECT status_id FROM ticket_status WHERE status_name='OPEN'");
                 ResultSet rs = ps.executeQuery()) {

                rs.next();
                openStatusId = rs.getLong(1);
            }

            try (PreparedStatement ps = con.prepareStatement(historySql)) {
                ps.setLong(1, ticketId);
                ps.setLong(2, openStatusId);
                ps.setLong(3, userId);
                ps.setString(4, "Ticket created");

                ps.executeUpdate();
            }

            con.commit();
            return ticketId;
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
