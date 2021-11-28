package com.epam.finalproject.cinema.service;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.dao.FilmDao;
import com.epam.finalproject.cinema.domain.dao.SessionDao;
import com.epam.finalproject.cinema.domain.entity.Film;
import com.epam.finalproject.cinema.domain.entity.Session;
import com.epam.finalproject.cinema.exception.DBException;
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

public class SessionService {
    private static SessionService instance = null;
    private final SessionDao sessionDao;
    private final FilmDao filmDao;
    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(FilmService.class);

    public SessionService() {
        sessionDao = SessionDao.getInstance();
        filmDao = FilmDao.getInstance();
        connectionPool = PostgresConnectionPool.getInstance();
    }

    public static synchronized SessionService getInstance() {
        if (instance == null) {
            instance = new SessionService();
        }
        return instance;
    }


    public List<SessionsInfoGroupByDate> getAllSessionsOfFilm(int filmId) throws DBException {
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


}