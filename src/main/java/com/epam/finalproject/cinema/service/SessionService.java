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
        List<SessionsInfoGroupByDate> filmSessionInfoList = null;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            List<Session> sessions = sessionDao.findCurrentFilmSessionsByFilmId(filmId, connection);
            Map<Integer, Integer> freeTicketsOfSessions = sessionDao.
                    findTicketIdCountOfFilmGroupBySessionId(filmId, connection);
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
        int removeIndex = 0;
        List<SessionsInfoGroupByDate> filmSessionInfoList = new ArrayList<>();
        for (int i = 0; i < sessions.size() - 1; i++) {
            Session session = sessions.get(i);
            SessionsInfoGroupByDate filmSessionInfo = getFilmSessionsInfo(session);
            filmSessionInfo.getSessionsInfo().add(getSessionPlacesInfo(session, freeTickets));
            for (int j = i + 1; j < sessions.size(); j++) {
                Session innerSession = sessions.get(j);

                if (isSessionsWithEqualDate(session, innerSession)) {
                    filmSessionInfo.getSessionsInfo().add(getSessionPlacesInfo(innerSession, freeTickets));
                    removeIndex = j;
                }

                if ((sessions.size() - 1) == j) {
                    SessionsInfoGroupByDate innerFilmSessionInfo = getFilmSessionsInfo(innerSession);
                    innerFilmSessionInfo.getSessionsInfo().add(getSessionPlacesInfo(innerSession, freeTickets));
                    filmSessionInfoList.add(innerFilmSessionInfo);
                }
            }
            if (removeIndex != 0)
                sessions.remove(removeIndex);
            filmSessionInfoList.add(filmSessionInfo);
        }

        return filmSessionInfoList;
    }

    private boolean isSessionsWithEqualDate(Session sessionA, Session sessionB) {
        LocalDate dateA = sessionA.getLocaleDateTime().toLocalDate();
        LocalDate dateB = sessionB.getLocaleDateTime().toLocalDate();
        return dateA.compareTo(dateB) == 0;
    }

    private SessionInfo getSessionPlacesInfo(Session session, Map<Integer, Integer> freeTickets) {
        int sessionId = session.getId();
        int freePlacesCount = freeTickets.get(sessionId);
        return new SessionInfo(sessionId, freePlacesCount,
                session.getLocaleDateTime().toLocalTime());
    }

    private SessionsInfoGroupByDate getFilmSessionsInfo(Session session) {
        return new SessionsInfoGroupByDate(session.getLocaleDateTime().toLocalDate(),
                session.getLang());
    }


}