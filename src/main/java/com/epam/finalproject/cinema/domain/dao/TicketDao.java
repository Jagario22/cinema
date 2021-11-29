package com.epam.finalproject.cinema.domain.dao;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.constants.PostgresQuery;
import com.epam.finalproject.cinema.domain.entity.Genre;
import com.epam.finalproject.cinema.domain.entity.Ticket;
import com.epam.finalproject.cinema.util.CloseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.epam.finalproject.cinema.domain.constants.PostgresQuery.INSERT_INTO_GENRES_VALUES;
import static com.epam.finalproject.cinema.domain.constants.PostgresQuery.INSERT_INTO_TICKETS_VALUES;

public class TicketDao {
    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(TicketDao.class);

    private TicketDao() {
        connectionPool = PostgresConnectionPool.getInstance();
    }

    private static TicketDao instance = null;

    public static synchronized TicketDao getInstance() {
        if (instance == null) {
            instance = new TicketDao();
        }
        return instance;
    }


    public void insert(Ticket ticket, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(INSERT_INTO_TICKETS_VALUES, Statement.RETURN_GENERATED_KEYS);
            ps.setShort(1, ticket.getNumber());
            ps.setInt(2, ticket.getTicketTypeId());
            ps.setInt(3, ticket.getSessionId());
            ps.setInt(4, ticket.getUserId());
            ps.execute();
            rs = ps.getGeneratedKeys();

            if (!rs.next()) {
                throw new SQLException("Inserting ticket " + ticket + " failed");
            }
        } catch (SQLException e) {
            log.error("Inserting ticket " + ticket + " failed");
            throw e;
        } finally {
            try {
                CloseUtil.close(ps, rs);
            } catch (SQLException e) {
                log.error("Inserting ticket " + ticket + " failed");
            }
        }

    }

    public List<Ticket> findTicketsBySessionId(int sessionId, Connection connection) throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(PostgresQuery.
                    SELECT_TICKETS_BY_SESSION_ID_WHERE_USER_ID_IS_NULL);
            ps.setInt(1, sessionId);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                Ticket ticket = readTicket(resultSet);
                tickets.add(ticket);
                while (resultSet.next()) {
                    ticket = readTicket(resultSet);
                    tickets.add(ticket);
                }
            }
        } finally {
            CloseUtil.close(ps, resultSet);
        }
        return tickets;
    }

    public Ticket findTicketByIdWhereUserIdIsNull(int id, Connection connection) throws SQLException {
        Ticket ticket = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(PostgresQuery.SELECT_TICKET_BY_ID_WHERE_USER_ID_IS_NULL);
            ps.setInt(1, id);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                ticket = readTicket(resultSet);
            }
        } finally {
            CloseUtil.close(ps, resultSet);
        }
        return ticket;
    }

    public void updateTicketOnUserIdById(int ticketId, int userId, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        int result = 0;
        try {
            ps = connection.prepareStatement(PostgresQuery.UPDATE_TICKET_ON_USER_ID_BY_ID);
            ps.setInt(1, userId);
            ps.setInt(2, ticketId);

            result = ps.executeUpdate();
            if (result != 1) {
                throw new SQLException("Updating wallet balance was failed");
            }
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
            ps = connection.prepareStatement(PostgresQuery.SELECT_TICKETS_BY_USER_ID);
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
            CloseUtil.close(ps, resultSet);
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
