package com.epam.finalproject.cinema.domain.dao;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.constants.PostgresQuery;
import com.epam.finalproject.cinema.domain.entity.Genre;
import com.epam.finalproject.cinema.domain.entity.TicketType;
import com.epam.finalproject.cinema.util.CloseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;

import static com.epam.finalproject.cinema.domain.constants.PostgresQuery.INSERT_INTO_GENRES_VALUES;
import static com.epam.finalproject.cinema.domain.constants.PostgresQuery.INSERT_INTO_TICKET_TYPES_VALUES;

public class TicketTypeDao {
    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(TicketTypeDao.class);

    private TicketTypeDao() {
        connectionPool = PostgresConnectionPool.getInstance();
    }

    private static TicketTypeDao instance = null;

    public static synchronized TicketTypeDao getInstance() {
        if (instance == null) {
            instance = new TicketTypeDao();
        }
        return instance;
    }

    public void insert(TicketType ticketType, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(INSERT_INTO_TICKET_TYPES_VALUES, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ticketType.getName());
            ps.setInt(2, ticketType.getPrice().intValue());
            ps.execute();
            rs = ps.getGeneratedKeys();

            if (!rs.next()) {
                throw new SQLException("Inserting ticketType " + ticketType + " failed");
            }
        } catch (SQLException e) {
            log.error("Inserting ticketType " + ticketType + " failed");
            throw e;
        } finally {
            try {
                CloseUtil.close(ps, rs);
            } catch (SQLException e) {
                log.error("Inserting ticketType " + ticketType + " failed");
            }
        }

    }

    public TicketType findById(int id, Connection connection) throws SQLException {
        TicketType ticketType;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(PostgresQuery.SELECT_TICKET_TYPE_BY_ID);
            ps.setInt(1, id);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                ticketType = readTicketType(resultSet);
            } else {
                String msg = "TicketType by id: " + id + " wasn't found";
                log.error(msg);
                throw new SQLException(msg);
            }
        } finally {
            CloseUtil.close(ps, resultSet);
        }
        return ticketType;
    }


    public TicketType readTicketType(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        String name = resultSet.getString(2);
        BigDecimal price = BigDecimal.valueOf(resultSet.getInt(3));
        return new TicketType(id, name, price);
    }
}
