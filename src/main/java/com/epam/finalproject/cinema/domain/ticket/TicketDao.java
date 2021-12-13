package com.epam.finalproject.cinema.domain.ticket;

import com.epam.finalproject.cinema.util.CloseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.epam.finalproject.cinema.domain.ticket.TicketQuery.*;
/**
 * Manager which provides methods for operation with ticket entity in DB.
 * Works with PostgresSQL dialect
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class TicketDao {
    private final static Logger log = LogManager.getLogger(TicketDao.class);

    private TicketDao() {
    }

    private static TicketDao instance = null;

    public static synchronized TicketDao getInstance() {
        if (instance == null) {
            instance = new TicketDao();
        }
        return instance;
    }

    public int insert(Ticket ticket, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int result;
        try {
            ps = connection.prepareStatement(INSERT_INTO_TICKETS_VALUES, Statement.RETURN_GENERATED_KEYS);
            ps.setShort(1, ticket.getNumber());
            ps.setInt(2, ticket.getTicketTypeId());
            ps.setInt(3, ticket.getSessionId());
            ps.execute();
            rs = ps.getGeneratedKeys();

            if (!rs.next()) {
                throw new SQLException("Inserting ticket " + ticket + " failed");
            }

            result = rs.getInt(1);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            try {
                CloseUtil.close(ps, rs);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        return result;
    }

    public List<Ticket> findTicketsBySessionIdWhereUserIsNull(int sessionId, Connection connection) throws
            SQLException {
        return findTicketsOfSession(SELECT_TICKETS_BY_SESSION_ID_WHERE_USER_ID_IS_NULL,
                sessionId, connection);
    }

    public List<Ticket> findTicketsOfSession(String sql, int sessionId, Connection connection) throws
            SQLException {
        List<Ticket> tickets = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, sessionId);
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Ticket ticket = readTicket(resultSet);
                tickets.add(ticket);
            }
        } finally {
            try {
                CloseUtil.close(ps, resultSet);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return tickets;
    }

    public List<Ticket> findAllTicketsOfSession(int sessionId, Connection connection) throws SQLException {
        return findTicketsOfSession(SELECT_TICKETS_OF_SESSION, sessionId, connection);
    }

    public Ticket findTicketByIdWhereUserIdIsNull(int id, Connection connection) throws SQLException {
        Ticket ticket = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(SELECT_TICKET_BY_ID_WHERE_USER_ID_IS_NULL);
            ps.setInt(1, id);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                ticket = readTicket(resultSet);
            }
        } finally {
            try {
                CloseUtil.close(ps, resultSet);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return ticket;
    }

    public void updateTicketOnUserIdById(int ticketId, int userId, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        int result;
        try {
            ps = connection.prepareStatement(UPDATE_TICKET_ON_USER_ID_BY_ID);
            ps.setInt(1, userId);
            ps.setInt(2, ticketId);

            result = ps.executeUpdate();
            if (result == 0) {
                throw new SQLException("Updating ticket was failed");
            }
        } catch (SQLException e) {
            log.error(e);
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    public List<Ticket> findTicketsByUserId(int userId, Connection connection) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(SELECT_TICKETS_BY_USER_ID);
            ps.setInt(1, userId);
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Ticket ticket = readTicket(resultSet);
                tickets.add(ticket);
            }

        } catch (SQLException e) {
            log.debug(e.getMessage());
            throw e;
        } finally {
            try {
                CloseUtil.close(ps, resultSet);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        return tickets;
    }


    private Ticket readTicket(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        short num = resultSet.getShort(2);
        int ticketTypeId = resultSet.getInt(3);
        int sessionId = resultSet.getInt(4);
        int userId = resultSet.getInt(5);
        return new Ticket(id, num, ticketTypeId, sessionId, userId);
    }
}
