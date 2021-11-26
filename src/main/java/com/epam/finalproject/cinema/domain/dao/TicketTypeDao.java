package com.epam.finalproject.cinema.domain.dao;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.constants.PostgresQuery;
import com.epam.finalproject.cinema.domain.entity.TicketType;
import com.epam.finalproject.cinema.util.CloseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
