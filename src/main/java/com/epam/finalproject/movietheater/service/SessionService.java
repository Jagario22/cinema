package com.epam.finalproject.movietheater.service;

import com.epam.finalproject.movietheater.domain.connection.ConnectionPool;
import com.epam.finalproject.movietheater.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.movietheater.domain.dao.FilmDao;
import com.epam.finalproject.movietheater.domain.dao.SessionDao;
import com.epam.finalproject.movietheater.domain.entity.Film;
import com.epam.finalproject.movietheater.domain.entity.Session;
import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.web.model.session.FilmSessionsInfo;
import com.epam.finalproject.movietheater.web.model.session.SessionInfo;
import com.epam.finalproject.movietheater.web.model.session.SessionPlacesInfo;
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


    public List<FilmSessionsInfo> getAllSessionsOfFilm(int filmId) throws DBException {
        List<FilmSessionsInfo> filmSessionsInfoList = null;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            List<Session> sessions = sessionDao.findCurrentFilmSessionsByFilmId(filmId, connection);
            Map<Integer, Integer> freeTicketsOfSessions = sessionDao.
                    findTicketIdCountOfFilmGroupBySessionId(filmId, connection);
            filmSessionsInfoList = groupSessionsBySessionDate(sessions, freeTicketsOfSessions);
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
        return filmSessionsInfoList;
    }

    public SessionInfo getSessionById(int sessionId) throws DBException {
        SessionInfo sessionInfo;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            Session session = sessionDao.findCurrentSessionById(sessionId, connection);
            Film film = filmDao.findFilmById(session.getFilmId(), connection);
            sessionInfo = new SessionInfo(sessionId, film, session.getLocaleDateTime(), session.getLang());
            connection.commit();
        } catch (SQLException | NamingException e) {
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
        return sessionInfo;

    }

    private List<FilmSessionsInfo> groupSessionsBySessionDate(List<Session> sessions,
                                                              Map<Integer, Integer> freeTickets) {
        int removeIndex = 0;
        List<FilmSessionsInfo> filmSessionsInfoList = new ArrayList<>();
        for (int i = 0; i < sessions.size() - 1; i++) {
            Session session = sessions.get(i);
            FilmSessionsInfo filmSessionsInfo = getFilmSessionsInfo(session);
            filmSessionsInfo.getSessionsPlacesInfo().add(getSessionPlacesInfo(session, freeTickets));
            for (int j = i + 1; j < sessions.size(); j++) {
                Session innerSession = sessions.get(j);

                if (isSessionsWithEqualDate(session, innerSession)) {
                    filmSessionsInfo.getSessionsPlacesInfo().add(getSessionPlacesInfo(innerSession, freeTickets));
                    removeIndex = j;
                }

                if ((sessions.size() - 1) == j) {
                    FilmSessionsInfo innerFilmSessionsInfo = getFilmSessionsInfo(innerSession);
                    innerFilmSessionsInfo.getSessionsPlacesInfo().add(getSessionPlacesInfo(innerSession, freeTickets));
                    filmSessionsInfoList.add(innerFilmSessionsInfo);
                }
            }
            if (removeIndex != 0)
                sessions.remove(removeIndex);
            filmSessionsInfoList.add(filmSessionsInfo);
        }

        return filmSessionsInfoList;
    }

    private boolean isSessionsWithEqualDate(Session sessionA, Session sessionB) {
        LocalDate dateA = sessionA.getLocaleDateTime().toLocalDate();
        LocalDate dateB = sessionB.getLocaleDateTime().toLocalDate();
        return dateA.compareTo(dateB) == 0;
    }

    private SessionPlacesInfo getSessionPlacesInfo(Session session, Map<Integer, Integer> freeTickets) {
        int sessionId = session.getId();
        int freePlacesCount = freeTickets.get(sessionId);
        return new SessionPlacesInfo(sessionId, freePlacesCount,
                session.getLocaleDateTime().toLocalTime());
    }

    private FilmSessionsInfo getFilmSessionsInfo(Session session) {
        return new FilmSessionsInfo(session.getLocaleDateTime().toLocalDate(), session.getId(),
                session.getLang());
    }

}
