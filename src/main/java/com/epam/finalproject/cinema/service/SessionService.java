package com.epam.finalproject.cinema.service;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.dao.FilmDao;
import com.epam.finalproject.cinema.domain.dao.SessionDao;
import com.epam.finalproject.cinema.domain.dao.TicketDao;
import com.epam.finalproject.cinema.domain.dao.TicketTypeDao;
import com.epam.finalproject.cinema.domain.entity.Film;
import com.epam.finalproject.cinema.domain.entity.Session;
import com.epam.finalproject.cinema.domain.entity.Ticket;
import com.epam.finalproject.cinema.domain.entity.TicketType;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.exception.IncorrectInputDataException;
import com.epam.finalproject.cinema.web.constants.CinemaConstants;
import com.epam.finalproject.cinema.web.model.film.session.SessionsInfoGroupByDate;
import com.epam.finalproject.cinema.web.model.film.session.SessionInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.epam.finalproject.cinema.web.constants.CinemaConstants.COUNT_OF_ROWS;
import static com.epam.finalproject.cinema.web.constants.CinemaConstants.COUNT_OF_ROW_SEAT;

public class SessionService {
    private static SessionService instance = null;
    private final SessionDao sessionDao;
    private final FilmDao filmDao;
    private final UserProfileService userProfileService;
    private final TicketDao ticketDao;
    private final TicketTypeDao ticketTypeDao;
    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(FilmService.class);

    public SessionService() {
        sessionDao = SessionDao.getInstance();
        ticketDao = TicketDao.getInstance();
        filmDao = FilmDao.getInstance();
        ticketTypeDao = TicketTypeDao.getInstance();
        userProfileService = UserProfileService.getInstance();
        connectionPool = PostgresConnectionPool.getInstance();
    }

    public static synchronized SessionService getInstance() {
        if (instance == null) {
            instance = new SessionService();
        }
        return instance;
    }

    public void createSession(Session session) throws DBException, IncorrectInputDataException {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            validateSessionForCreating(session, connection);
            int sessionId = sessionDao.insert(session, connection);
            int preLastRowFirstSeat = COUNT_OF_ROW_SEAT * COUNT_OF_ROWS - COUNT_OF_ROWS + 1;
            for (int i = 1; i < preLastRowFirstSeat; i++) {
                Ticket ticket = new Ticket((short) i, 1, sessionId, null);
                ticketDao.insert(ticket, connection);
            }

            for (int i = preLastRowFirstSeat; i <= COUNT_OF_ROWS * COUNT_OF_ROW_SEAT; i++) {
                Ticket ticket = new Ticket((short) i, 2, sessionId, null);
                ticketDao.insert(ticket, connection);
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Creating session failed";
            log.error(msg + "\n" + e.getMessage());
            connectionRollback(connection, msg);
            throw new DBException(msg, e);
        } finally {
            String msg = "Creating session failed";
            connectionClose(connection, msg);
        }
    }

    private void validateSessionForCreating(Session session, Connection connection) throws SQLException, DBException {
        String msgError = "Creating session is failed";
        if (!isUniqueSession(connection, session)) {
            connectionRollback(connection, msgError);
            throw new IncorrectInputDataException("Session is not unique");
        }
        if (session.getFilmId() < 1) {
            connectionRollback(connection, msgError);
            throw new IncorrectInputDataException("Film id is less than 1");
        }
        if (!isFilmExist(connection, session.getFilmId())) {
            connectionRollback(connection, msgError);
            throw new IncorrectInputDataException("Film with id " + session.getFilmId() + " doesn't exist");
        }
        validateDateTime(session.getLocalDateTime(), connection, msgError);
    }

    private boolean isFilmExist(Connection connection, int filmId) throws SQLException {
        Film film = filmDao.findFilmById(filmId, connection);
        return film != null;
    }

    public boolean isUniqueSession(Connection connection, Session session) throws SQLException {
        Session result = sessionDao.findSessionWhereDateTimeIs(session.getLocalDateTime(), connection);
        return result == null;
    }

    public void validateDateTime(LocalDateTime datetime, Connection connection, String msgError) throws SQLException, DBException {
        if (datetime.isBefore(LocalDateTime.now())) {
            connectionRollback(connection, msgError);
            throw new IncorrectInputDataException("Date time can't be before now");
        }
        LocalTime time = datetime.toLocalTime();
        if (time.isBefore(CinemaConstants.START_TIME) && time.isAfter(CinemaConstants.END_TIME)) {
            connectionRollback(connection, msgError);
            throw new IncorrectInputDataException("Date time can't be before now");
        }
        Session session = sessionDao.findNearestSessionWhereDatetimeBefore(datetime, connection);
        if (session == null)
            return;

        Film film = filmDao.findFilmById(session.getFilmId(), connection);
        LocalDateTime nearestTime = datetime.plusMinutes(film.getLen() + CinemaConstants.BREAK_MINS);
        if (!datetime.isAfter(nearestTime)) {
            connectionRollback(connection, msgError);
            throw new IncorrectInputDataException("The previous session has not finished showing yet or break isn't" +
                    " over yet");
        }
    }

    public void cancelSessionById(int id) throws DBException {
        Connection connection = null;
        try {
            if (id < 1) {
                throw new IllegalArgumentException("Session id is less than 1");
            }
            connection = connectionPool.getConnection();
            List<Ticket> tickets = ticketDao.findAllTicketsOfSession(id, connection);
            List<Ticket> userTickets = tickets.stream().filter(t -> t.getUserId() != 0).collect(Collectors.toList());
            for (Ticket ticket : userTickets) {
                TicketType ticketType = ticketTypeDao.findById(ticket.getTicketTypeId(), connection);
                userProfileService.topUpBalanceByUserId(ticket.getUserId(), ticketType.getPrice(), connection);
            }
            sessionDao.deleteById(id, connection);
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Canceling film failed";
            connectionRollback(connection, msg);
            log.error(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
        } finally {
            connectionClose(connection, "Canceling film failed");
        }
    }

    public List<SessionInfo> getAllCurrentSessionsInfo() throws DBException {
        List<SessionInfo> sessionsInfo = new ArrayList<>();
        Connection connection = null;

        try {
            connection = connectionPool.getConnection();
            List<Session> sessions = sessionDao.findSessionsAfterDate(LocalDateTime.now(), connection);
            for (Session session : sessions) {
                Film film = filmDao.findFilmById(session.getFilmId(), connection);
                SessionInfo sessionInfo = getSessionInfo(session.getId(), film, session);
                sessionsInfo.add(sessionInfo);
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Getting all sessions failed";
            log.error(msg + "\n" + e.getMessage());
            connectionRollback(connection, msg);
            throw new DBException(msg, e);
        } finally {
            String msg = "Getting all sessions failed";
            connectionClose(connection, msg);
        }

        return sessionsInfo;
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
            sessionInfo = getSessionInfo(sessionId, film, session);
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
            String msg = "Getting session failed";
            connectionClose(connection, msg);
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
        SessionsInfoGroupByDate filmSessionInfo = getSessionInfoGroupByDate(session);
        filmSessionInfo.getSessionsInfo().add(getSessionPlacesInfo(session, freeTickets));
        filmSessionInfoList.add(filmSessionInfo);
    }


    private void groupSession(List<SessionsInfoGroupByDate> filmSessionInfoList, Session session,
                              Map<Integer, Integer> freeTickets) {
        boolean containsDate = false;
        for (SessionsInfoGroupByDate groupedSessionInfo : filmSessionInfoList) {
            if (isEqualDates(groupedSessionInfo.getDate(), session.getLocalDateTime().toLocalDate())) {
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

    private SessionInfo getSessionInfo(int sessionId, Film film, Session session) {
        return new SessionInfo(sessionId, film, session.getLocalDateTime(), session.getLang());
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
                session.getLocalDateTime().toLocalTime());
    }

    private SessionsInfoGroupByDate getSessionInfoGroupByDate(Session session) {
        return new SessionsInfoGroupByDate(session.getLocalDateTime().toLocalDate(),
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