package com.epam.finalproject.cinema.service;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.dao.FilmDao;
import com.epam.finalproject.cinema.domain.dao.SessionDao;
import com.epam.finalproject.cinema.domain.dao.TicketDao;
import com.epam.finalproject.cinema.domain.entity.Film;
import com.epam.finalproject.cinema.domain.entity.Session;
import com.epam.finalproject.cinema.domain.entity.Ticket;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.web.constants.CinemaConstants;
import com.epam.finalproject.cinema.web.model.film.session.SessionsInfoGroupByDate;
import com.epam.finalproject.cinema.web.model.film.session.SessionInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.epam.finalproject.cinema.web.constants.CinemaConstants.COUNT_OF_ROW_SEAT;

public class SessionService {
    private static SessionService instance = null;
    private final SessionDao sessionDao;
    private final FilmDao filmDao;
    private final TicketDao ticketDao;
    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(FilmService.class);

    public SessionService() {
        sessionDao = SessionDao.getInstance();
        ticketDao = TicketDao.getInstance();
        filmDao = FilmDao.getInstance();
        connectionPool = PostgresConnectionPool.getInstance();
    }

    public static synchronized SessionService getInstance() {
        if (instance == null) {
            instance = new SessionService();
        }
        return instance;
    }

    public void createSession(Session session) throws DBException {
        Connection connection = null;
        int preLastRow = CinemaConstants.COUNT_OF_ROWS;
        try {
            connection = connectionPool.getConnection();
            sessionDao.insert(session, connection);
            for (int i = 1; i <= COUNT_OF_ROW_SEAT * preLastRow; i++) {
                Ticket ticket = new Ticket((short) i, 1, session.getId(), null);
                ticketDao.insert(ticket, connection);
            }

            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Creating sessions failed";
            log.error(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                String msg = "Creating sessions failed";
                log.error(msg + "\n" + e.getMessage());
            }
        }
    }

    public void cancelFilmById(int filmId) throws DBException {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            sessionDao.deleteByFilmId(filmId, connection);
            connection.commit();
        } catch (SQLException | NamingException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    String msg = "Canceling film failed";
                    log.error(msg + "\n" + e.getMessage());
                    throw new DBException(msg, e);
                }
            }
            String msg = "Canceling film failed";
            log.error(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("closing connection failed" + "\n" + e.getMessage());
                }
            }

        }
    }

    public List<Session> getAllCurrentSessionsOfFilm(int filmId, Connection connection) throws DBException {
        List<Session> sessions;
        String errorMsg = "Getting all sessions of film failed";
        try {
            sessions = sessionDao.findCurrentFilmSessionsByFilmId(filmId, connection);
        } catch (SQLException | NamingException e) {
            log.error(errorMsg + "\n" + e.getMessage());
            connectionRollback(connection, errorMsg);
            throw new DBException(errorMsg, e);
        }

        return sessions;
    }


    public List<SessionsInfoGroupByDate> getAllSessionsOfFilmGroupByDate(int filmId) throws DBException {
        List<SessionsInfoGroupByDate> filmSessionInfoList = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            List<Session> sessions = sessionDao.findCurrentFilmSessionsByFilmId(filmId, connection);
            Map<Integer, Integer> freeTicketsOfSessions = sessionDao.
                    findTicketIdCountOfFilmGroupBySessionId(filmId, connection);
            if (sessions.size() != 0)
                filmSessionInfoList = groupSessionsBySessionDate(sessions, freeTicketsOfSessions);
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Getting all sessions of film failed";
            log.error(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                String msg = "Getting session of film failed";
                log.error(msg + "\n" + e.getMessage());
            }
        }
        return filmSessionInfoList;
    }

    public SessionInfo getCurrentSessionInfoById(int sessionId, Connection connection) throws DBException {
        SessionInfo sessionInfo;
        try {
            Session session = sessionDao.findCurrentSessionById(sessionId, connection);
            Film film = filmDao.findFilmById(session.getFilmId(), connection);
            sessionInfo = new SessionInfo(sessionId, film, session.getLocaleDateTime(), session.getLang());
            connection.commit();
        } catch (SQLException e) {
            String msg = "Getting session failed";
            log.error(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
        }
        return sessionInfo;
    }

    public SessionInfo getCurrentSessionInfoById(int sessionId) throws DBException {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            return getCurrentSessionInfoById(sessionId, connection);
        } catch (NamingException | SQLException e) {
            String msg = "Getting session failed";
            log.error(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                String msg = "Getting session failed";
                log.error(msg + "\n" + e.getMessage());
            }
        }
    }

    public Session getCurrentSessionById(int sessionId, Connection connection) throws DBException {
        try {
            return sessionDao.findCurrentSessionById(sessionId, connection);
        } catch (SQLException e) {
            String msg = "Getting session failed";
            log.error(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
        }
    }

    private List<SessionsInfoGroupByDate> groupSessionsBySessionDate(List<Session> sessions,
                                                                     Map<Integer, Integer> freeTickets) {
        List<SessionsInfoGroupByDate> filmSessionInfoList = new ArrayList<>();
        addSessionToFilmSessionInfoList(filmSessionInfoList, sessions.get(0), freeTickets);
        for (int i = 1; i < sessions.size(); i++) {
            groupSession(filmSessionInfoList, sessions.get(i), freeTickets);
        }

        return filmSessionInfoList;
    }

    private void addSessionToFilmSessionInfoList(List<SessionsInfoGroupByDate> filmSessionInfoList,
                                                 Session session, Map<Integer, Integer> freeTickets) {
        SessionsInfoGroupByDate filmSessionInfo = getFilmSessionsInfo(session);
        filmSessionInfo.getSessionsInfo().add(getSessionPlacesInfo(session, freeTickets));
        filmSessionInfoList.add(filmSessionInfo);
    }


    private void groupSession(List<SessionsInfoGroupByDate> filmSessionInfoList, Session session,
                              Map<Integer, Integer> freeTickets) {
        boolean containsDate = false;
        for (SessionsInfoGroupByDate groupedSessionInfo : filmSessionInfoList) {
            if (isEqualDates(groupedSessionInfo.getDate(), session.getLocaleDateTime().toLocalDate())) {
                if (!isAlreadyContainSession(groupedSessionInfo.getSessionsInfo(), session)) {
                    groupedSessionInfo.getSessionsInfo().add(getSessionPlacesInfo(session, freeTickets));
                    containsDate = true;
                    break;
                }
            }
        }

        if (!containsDate) {
            addSessionToFilmSessionInfoList(filmSessionInfoList, session, freeTickets);
        }
    }

    private boolean isAlreadyContainSession(List<SessionInfo> sessionInfoList, Session session) {
        for (SessionInfo sessionInfo : sessionInfoList) {
            if (sessionInfo.isEqualToSession(session))
                return true;
        }

        return false;
    }

    private boolean isEqualDates(LocalDate A, LocalDate B) {
        return A.compareTo(B) == 0;
    }

    private SessionInfo getSessionPlacesInfo(Session session, Map<Integer, Integer> freeTickets) {
        int sessionId = session.getId();
        int freePlacesCount = 0;
        if (freeTickets.get(sessionId) != null)
            freePlacesCount = freeTickets.get(sessionId);
        return new SessionInfo(sessionId, freePlacesCount,
                session.getLocaleDateTime().toLocalTime());
    }

    private SessionsInfoGroupByDate getFilmSessionsInfo(Session session) {
        return new SessionsInfoGroupByDate(session.getLocaleDateTime().toLocalDate(),
                session.getLang());
    }

    private void connectionClose(Connection connection, String errorMsg) throws DBException {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DBException(errorMsg, e);
        }
    }

    private void connectionRollback(Connection connection, String errorMsg) throws DBException {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DBException(errorMsg, e);
        }
    }


}