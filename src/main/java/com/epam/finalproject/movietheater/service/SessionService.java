package com.epam.finalproject.movietheater.service;

import com.epam.finalproject.movietheater.domain.connection.ConnectionPool;
import com.epam.finalproject.movietheater.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.movietheater.domain.dao.FilmDao;
import com.epam.finalproject.movietheater.domain.dao.SessionDao;
import com.epam.finalproject.movietheater.domain.entity.Film;
import com.epam.finalproject.movietheater.domain.entity.Session;
import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.web.model.FilmSessionsDTO;
import com.epam.finalproject.movietheater.web.model.SessionDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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


    public List<FilmSessionsDTO> getAllSessionsOfFilm(int filmId) throws DBException {
        List<FilmSessionsDTO> filmSessionsDTOList = null;
        try {
            List<Session> sessions = sessionDao.findCurrentFilmSessionsByFilmId(filmId);
            filmSessionsDTOList = groupSessionsBySessionDate(sessions);
        } catch (SQLException | NamingException e) {
            String msg = "Getting all sessions of film failed";
            log.error(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
        }
        return filmSessionsDTOList;
    }

    public SessionDTO getSessionById(int sessionId) throws DBException {
        SessionDTO sessionDTO;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            Session session = sessionDao.findCurrentSessionById(sessionId, connection);
            Film film = filmDao.findFilmById(session.getFilmId(), connection);
            sessionDTO = new SessionDTO(sessionId, film, session.getLocaleDateTime(), session.getLang());
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
        return sessionDTO;

    }

    private List<FilmSessionsDTO> groupSessionsBySessionDate(List<Session> sessions) {
        int removeIndex = 0;
        List<FilmSessionsDTO> filmSessionsDTOList = new ArrayList<>();
        for (int i = 0; i < sessions.size() - 1; i++) {
            Session session = sessions.get(i);
            FilmSessionsDTO filmSessionsDTO = new FilmSessionsDTO(session.getLocaleDateTime().toLocalDate(), session.getId(),
                    session.getLang());
            filmSessionsDTO.setTimeList(new ArrayList<>());
            filmSessionsDTO.getTimeList().add(session.getLocaleDateTime().toLocalTime());
            for (int j = i + 1; j < sessions.size(); j++) {
                Session innerSession = sessions.get(j);

                if (isSessionsWithEqualDate(session, innerSession)) {
                    filmSessionsDTO.getTimeList().add(innerSession.
                            getLocaleDateTime().toLocalTime());
                    removeIndex = j;
                }
            }
            sessions.remove(removeIndex);
            filmSessionsDTOList.add(filmSessionsDTO);
        }

        return filmSessionsDTOList;
    }

    private boolean isSessionsWithEqualDate(Session sessionA, Session sessionB) {
        LocalDate dateA = sessionA.getLocaleDateTime().toLocalDate();
        LocalDate dateB = sessionB.getLocaleDateTime().toLocalDate();
        return dateA.compareTo(dateB) == 0;

    }
}
