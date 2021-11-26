package com.epam.finalproject.cinema.domain.dao;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.constants.PostgresQuery;
import com.epam.finalproject.cinema.domain.entity.Session;
import com.epam.finalproject.cinema.domain.entity.Session.Lang;
import com.epam.finalproject.cinema.util.CloseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.epam.finalproject.cinema.domain.constants.PostgresQuery.SELECT_CURRENT_SESSIONS_OF_FILM;
import static com.epam.finalproject.cinema.domain.constants.PostgresQuery.SELECT_TICKET_COUNT_OF_FILM_WHERE_USER_IS_NULL_GROUP_BY_SESSION_ID;

public class SessionDao {
    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(FilmDao.class);

    private SessionDao() {
        connectionPool = PostgresConnectionPool.getInstance();
    }

    private static SessionDao instance = null;

    public static synchronized SessionDao getInstance() {
        if (instance == null) {
            instance = new SessionDao();
        }
        return instance;
    }


    public  List<Session > findCurrentFilmSessionsByFilmId(int filmId, Connection connection) throws SQLException, NamingException {
        List<Session> currentSessions = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(SELECT_CURRENT_SESSIONS_OF_FILM);
            ps.setInt(1, filmId);
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Session session = readSession(resultSet);
                currentSessions.add(session);
            }
            connection.commit();
        } finally {
            CloseUtil.close(ps, resultSet);
        }

        return currentSessions;
    }

    public Map<Integer, Integer> findTicketIdCountOfFilmGroupBySessionId(int filmId, Connection connection) throws SQLException {
        ResultSet rs = null;
        PreparedStatement ps = null;
        Map<Integer, Integer> countTicketOfSession = new HashMap<>();
        try {
            ps = connection.prepareStatement(SELECT_TICKET_COUNT_OF_FILM_WHERE_USER_IS_NULL_GROUP_BY_SESSION_ID);
            ps.setInt(1, filmId);

            rs = ps.executeQuery();

            while (rs.next()) {
                int sessionId = rs.getInt(1);
                int ticketCount = rs.getInt(2);
                countTicketOfSession.put(sessionId, ticketCount);
            }
        } finally {
            CloseUtil.close(ps, rs);
        }
        return countTicketOfSession;
    }

    public Session findCurrentSessionById(int id, Connection connection) throws SQLException {
        Session session = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(PostgresQuery.SELECT_CURRENT_SESSION_BY_ID);
            ps.setInt(1, id);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                session = readSession(resultSet);
            }
        } finally {
            CloseUtil.close(ps, resultSet);
        }
        return session;
    }

    public Session findById(int id, Connection connection) throws SQLException {
        Session session = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(PostgresQuery.SELECT_SESSION_BY_ID);
            ps.setInt(1, id);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                session = readSession(resultSet);
            }
        } finally {
            CloseUtil.close(ps, resultSet);
        }
        return session;
    }

    private Session readSession(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        int filmId = rs.getInt(2);
        LocalDateTime date = rs.getTimestamp(3).toLocalDateTime();
        Lang lang = Lang.valueOf(rs.getString(4).toUpperCase());
        return new Session(id, filmId, date, lang);
    }



}
