package com.epam.finalproject.cinema.service;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.film.FilmDao;
import com.epam.finalproject.cinema.domain.Genre.GenreDao;
import com.epam.finalproject.cinema.domain.session.SessionDao;
import com.epam.finalproject.cinema.domain.film.Film;
import com.epam.finalproject.cinema.domain.Genre.Genre;
import com.epam.finalproject.cinema.domain.session.Session;
import com.epam.finalproject.cinema.domain.session.SessionQuery;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.web.constants.Params;
import com.epam.finalproject.cinema.web.model.film.FilmInfo;
import com.epam.finalproject.cinema.web.model.film.FilmStatistic;
import com.epam.finalproject.cinema.web.model.film.session.SessionInfo;
import com.epam.finalproject.cinema.web.model.ticket.TicketInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.print.ServiceDialog;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.epam.finalproject.cinema.domain.film.FilmQuery.FILM_TITLE_FIELD;
/**
 * Provides interactions between a client App and film database entity, that allows interaction between film,
 * genre and ticket and session entities.
 * Provides operations with Film DTO object.

 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class FilmService {
    private static FilmService instance = null;
    private FilmDao filmDao;
    private GenreDao genreDao;
    private TicketService ticketService;
    private SessionDao sessionDao;
    private ConnectionPool connectionPool;

    private final static Logger log = LogManager.getLogger(FilmService.class);

    public FilmService() {
        filmDao = FilmDao.getInstance();
        genreDao = GenreDao.getInstance();
        ticketService = TicketService.getInstance();
        sessionDao = SessionDao.getInstance();
        connectionPool = PostgresConnectionPool.getInstance();
    }


    public static synchronized FilmService getInstance() {
        if (instance == null) {
            instance = new FilmService();
        }
        return instance;
    }

    public void create(FilmInfo filmInfo) throws DBException {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            int filmId = filmDao.insert(filmInfo.getFilm(), connection);

            for (Genre genre : filmInfo.getGenres()) {
                filmDao.insertGenreOfFilm(genre.getId(), filmId, connection);
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Creating film failed filmInfo: " + filmInfo;
            log.error(msg + "\n" + e.getMessage());
            connectionRollback(connection, msg);
            throw new DBException(msg, e);
        } finally {
            connectionClose(connection, "Creating film failed");
        }
    }


    public List<Film> getAllCurrentFilmsSortedByAndBetween(String field, LocalDateTime startDateTime,
                                                           LocalDateTime endDateTime,
                                                           int offset, int limit) throws DBException {
        List<Film> currentFilms;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            switch (field) {
                case Params.FREE_PLACES_FIELD:
                    currentFilms = filmDao.findAllCurrentOrderByFreePlaces(startDateTime, endDateTime, offset, limit,
                            connection);
                    break;
                case Params.TITLE_FIELD:
                    currentFilms = filmDao.findAllCurrentOrderByField(FILM_TITLE_FIELD,
                            startDateTime, endDateTime, offset, limit, connection);
                    break;
                default:
                    currentFilms = filmDao.findAllCurrentOrderByField(SessionQuery.SESSIONS_DATE_TIME_FIELD,
                            startDateTime, endDateTime, offset, limit, connection);
                    break;
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Getting all current films failed";
            connectionRollback(connection, msg);
            log.error(msg);
            throw new DBException(msg, e);
        } finally {
            connectionClose(connection, "Getting all current films failed");
        }
        return currentFilms;
    }

    public int getAllCurrentFilmsCount(String field, LocalDateTime startDateTime,
                                       LocalDateTime endDateTime) throws DBException {
        Connection connection = null;
        int count;
        try {
            connection = connectionPool.getConnection();
            if (Params.FREE_PLACES_FIELD.equals(field)) {
                count = filmDao.findCountOfAllCurrentOrderByFreePlaces(startDateTime, endDateTime, connection);
            } else {
                count = filmDao.findCountOfAllCurrent(startDateTime, endDateTime, connection);
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Getting all current films failed";
            connectionRollback(connection, msg);
            log.error(msg);
            throw new DBException(msg, e);
        } finally {
            connectionClose(connection, "Getting all current films failed");
        }
        return count;
    }


    public FilmInfo getById(int id) throws DBException {
        Film film;
        List<Genre> genres;
        FilmInfo filmInfo;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            film = filmDao.findById(id, connection);
            genres = genreDao.findGenresByFilmId(film.getId(), connection);
            filmInfo = new FilmInfo(film, genres);
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Getting film by id " + id + " failed";
            connectionRollback(connection, msg);
            log.error(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
        } finally {
            connectionClose(connection, "Getting film by id " + id + " failed");

        }
        return filmInfo;
    }

    public List<FilmStatistic> getCurrentFilmsStatistic(int offset, int limit) throws DBException {
        List<FilmStatistic> filmsStatistic = new ArrayList<>();
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            List<Film> currentFilms = filmDao.findAllCurrentOrderByField(SessionQuery.SESSIONS_DATE_TIME_FIELD,
                    LocalDateTime.now(), null, offset, limit, connection);
            for (Film film : currentFilms) {
                FilmInfo filmInfo = getById(film.getId());
                List<Session> sessions = sessionDao.findCurrentFilmSessionsByFilmId(film.getId(), connection);
                FilmStatistic filmStatistic = new FilmStatistic();
                List<SessionInfo> sessionsInfo = new ArrayList<>();
                filmStatistic.setFilmInfo(filmInfo);
                for (Session session : sessions) {
                    List<TicketInfo> tickets = ticketService.getTicketsInfoBySessionId(session.getId(), connection);
                    SessionInfo sessionInfo = new SessionInfo(session.getId(), film, session.getLocalDateTime(),
                            session.getLang());
                    sessionInfo.setBoughtTicketsCount((int)
                            tickets.stream().filter(t -> t.getUserId() != 0).count());
                    sessionInfo.setCountOfTickets(tickets.size());
                    sessionsInfo.add(sessionInfo);
                }
                filmStatistic.setSessions(sessionsInfo);
                filmsStatistic.add(filmStatistic);
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Getting film statistic failed";
            connectionRollback(connection, msg);
            log.error(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
        } finally {
            String msg = "Getting film statistic failed";
            connectionClose(connection, msg);
        }
        return filmsStatistic;
    }

    public List<FilmInfo> getAllFilms() throws DBException {
        Connection connection = null;
        List<FilmInfo> filmsInfo = new ArrayList<>();
        try {
            connection = connectionPool.getConnection();
            List<Film> films = filmDao.findAll(connection);
            for (Film film : films) {
                FilmInfo filmInfo = getById(film.getId());
                filmsInfo.add(filmInfo);
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Getting all films failed";
            connectionRollback(connection, msg);
            log.error(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
        } finally {
            String msg = "Getting all films failed";
            connectionClose(connection, msg);
        }
        return filmsInfo;
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

    public void setFilmDao(FilmDao filmDao) {
        this.filmDao = filmDao;
    }

    public void setGenreDao(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public void setSessionDao(SessionDao sessionDao) {
        this.sessionDao = sessionDao;
    }

    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
}
