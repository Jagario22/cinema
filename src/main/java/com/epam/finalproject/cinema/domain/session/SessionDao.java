package com.epam.finalproject.cinema.domain.session;

import com.epam.finalproject.cinema.domain.session.Session.Lang;
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

import static com.epam.finalproject.cinema.domain.session.SessionQuery.*;
import static com.epam.finalproject.cinema.domain.wallet.WalletQuery.SELECT_TICKET_COUNT_OF_FILM_WHERE_USER_IS_NULL_GROUP_BY_SESSION_ID;
/**
 * Manager which provides methods for operation with session entity in DB.
 * Works with PostgresSQL dialect
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class SessionDao {
    private final static Logger log = LogManager.getLogger(SessionDao.class);

    private static SessionDao instance = null;

    public static synchronized SessionDao getInstance() {
        if (instance == null) {
            instance = new SessionDao();
        }
        return instance;
    }

    public void deleteById(int id, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(DELETE_FROM_SESSIONS_WHERE_ID_IS);
            ps.setInt(1, id);
            int index = ps.executeUpdate();

            if (index == 0) {
                throw new SQLException("Deleting session by Id " + id + " failed");
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                log.error("Deleting session by id " + id + " failed");
            }
        }
    }

    public int insert(Session session, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int resultId;
        try {
            ps = connection.prepareStatement(INSERT_INTO_SESSIONS, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, Timestamp.valueOf(session.getLocalDateTime()));
            ps.setString(2, session.getLang().toString().toLowerCase());
            ps.setInt(3, session.getFilmId());

            ps.execute();
            rs = ps.getGeneratedKeys();

            if (!rs.next()) {
                throw new SQLException("Inserting session " + session + " failed");
            } else {
                resultId = rs.getInt(1);
            }
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
        return resultId;
    }

    public Session findNearestSessionWhereDatetimeBefore(LocalDateTime dateTime, Connection connection) throws SQLException {
        return getSession(dateTime, connection, SELECT_SESSION_WITH_MAX_DATETIME_BEFORE);
    }

    public List<Session> findCurrentFilmSessionsByFilmId(int filmId, Connection connection) throws SQLException {
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
            ps = connection.prepareStatement(SELECT_CURRENT_SESSION_BY_ID);
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

    public List<Session> findSessionsAfterDate(LocalDateTime localDateTime, Connection connection) throws SQLException {
        List<Session> sessions = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(SELECT_SESSIONS_AFTER_DATE);
            ps.setTimestamp(1, Timestamp.valueOf(localDateTime));
            rs = ps.executeQuery();

            while (rs.next()) {
                Session session = readSession(rs);
                sessions.add(session);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            CloseUtil.close(ps, rs);
        }

        return sessions;
    }

    public Session findSessionByDatetime(LocalDateTime localDateTime, Connection connection) throws SQLException {
        return getSession(localDateTime, connection, SELECT_FROM_SESSIONS_WHERE_DATETIME);
    }

    private Session getSession(LocalDateTime localDateTime, Connection connection,
                               String selectFromSessionsWhereDatetime) throws SQLException {
        Session session = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(selectFromSessionsWhereDatetime);
            ps.setTimestamp(1, Timestamp.valueOf(localDateTime));
            rs = ps.executeQuery();

            if (rs.next()) {
                session = readSession(rs);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            CloseUtil.close(ps, rs);
        }

        return session;
    }
}
