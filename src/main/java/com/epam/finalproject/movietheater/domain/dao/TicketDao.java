package com.epam.finalproject.movietheater.domain.dao;

import com.epam.finalproject.movietheater.domain.connection.ConnectionPool;
import com.epam.finalproject.movietheater.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.movietheater.domain.constants.PostgresQuery;
import com.epam.finalproject.movietheater.domain.entity.Ticket;
import com.epam.finalproject.movietheater.domain.util.CloseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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



    private Ticket readTicket(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        short num = resultSet.getShort(2);
        int ticketTypeId = resultSet.getInt(3);
        return new Ticket(id, num, ticketTypeId);
    }

}
