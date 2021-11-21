package com.epam.finalproject.movietheater.service;

import com.epam.finalproject.movietheater.domain.dao.SessionDao;
import com.epam.finalproject.movietheater.domain.entity.Session;
import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.web.model.SessionDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SessionService {
    private static SessionService instance = null;
    private final SessionDao sessionDao;
    private final static Logger log = LogManager.getLogger(FilmService.class);

    public SessionService() {
        sessionDao = SessionDao.getInstance();
    }

    public static synchronized SessionService getInstance() {
        if (instance == null) {
            instance = new SessionService();
        }
        return instance;
    }


    public List<SessionDTO> getAllSessionsOfFilm(int filmId) throws DBException {
        List<SessionDTO> sessionDTOList = null;
        try {
            List<Session> sessions = sessionDao.findCurrentFilmSessionsByFilmId(filmId);
            sessionDTOList = groupSessionsBySessionDate(sessions);
        } catch (SQLException | NamingException e) {
            String msg = "Getting all sessions of film failed";
            log.error(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
        }
        return sessionDTOList;
    }

    private List<SessionDTO> groupSessionsBySessionDate(List<Session> sessions) {
        List<SessionDTO> sessionDTOList = new ArrayList<>();
        for (int i = 0; i < sessions.size() - 1; i++) {
            Session session = sessions.get(i);
            SessionDTO sessionDTO = new SessionDTO(session.getLocaleDateTime().toLocalDate(), session.getId(),
                    session.getLang());
            sessionDTO.setTimeList(new ArrayList<>());
            for (int j = i + 1; j < sessions.size(); j++) {
                Session innerSession = sessions.get(j);

                if (isSessionsWithEqualDate(session, innerSession)) {
                    sessionDTO.getTimeList().add(innerSession.
                            getLocaleDateTime().toLocalTime());
                }
            }
            sessionDTOList.add(sessionDTO);
        }

        return distinctSessionDTOList(sessionDTOList);
    }

    private boolean isSessionsWithEqualDate(Session sessionA, Session sessionB) {
        LocalDate dateA = sessionA.getLocaleDateTime().toLocalDate();
        LocalDate dateB = sessionB.getLocaleDateTime().toLocalDate();
        return dateA.compareTo(dateB) == 0;

    }


    private List<SessionDTO> distinctSessionDTOList(List<SessionDTO> sessionDTOList) {
        for (int i = 0; i < sessionDTOList.size(); i++) {
            for (int j = i; j < sessionDTOList.size(); j++) {
                if (sessionDTOList.get(i).getDate()
                        .compareTo(sessionDTOList.get(j).getDate()) == 0) {
                    sessionDTOList.remove(sessionDTOList.get(j));
                }
            }
        }
        return sessionDTOList;
    }
}
