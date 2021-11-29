package com.epam.finalproject.cinema.service;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.constants.PostgresQuery;
import com.epam.finalproject.cinema.domain.dao.FilmDao;
import com.epam.finalproject.cinema.domain.dao.GenreDao;
import com.epam.finalproject.cinema.domain.entity.Film;
import com.epam.finalproject.cinema.domain.entity.Genre;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.web.constants.Params;
import com.epam.finalproject.cinema.web.model.film.FilmInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class FilmService {
    private static FilmService instance = null;
    private final FilmDao filmDao;
    private final GenreDao genreDao;
    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(FilmService.class);

    public FilmService() {
        filmDao = FilmDao.getInstance();
        genreDao = GenreDao.getInstance();
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
            filmDao.insert(filmInfo.getFilm(), connection);
            for (Genre genre : filmInfo.getGenres()) {
                filmDao.insertGenreOfFilm(genre.getId(), filmInfo.getFilm().getId(), connection);
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Creating film failed";
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


    public List<Film> getAllCurrentFilmsSortedByAndBetween(String field, LocalDateTime startDateTime,
                                                           LocalDateTime endDateTime, int offset, int limit) throws DBException {
        List<Film> currentFilms;
        try {
            switch (field) {
                case Params.FREE_PLACES_FIELD:
                    currentFilms = filmDao.getAllCurrentFilmsOrderByPlaces(startDateTime, endDateTime, offset, limit);
                    break;
                case Params.TITLE_FIELD:
                    currentFilms = filmDao.getAllCurrentFilmsOrderBy(PostgresQuery.FILM_TITLE_FIELD,
                            startDateTime, endDateTime, offset, limit);
                    break;
                default:
                    currentFilms = filmDao.getAllCurrentFilmsOrderBy(PostgresQuery.SESSIONS_DATE_TIME_FIELD,
                            startDateTime, endDateTime, offset, limit);
                    break;
            }
        } catch (SQLException | NamingException | IOException e) {
            String msg = "Getting all current films failed";
            log.error(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
        }
        return currentFilms;
    }

    public int getAllCurrentFilmsCount(String field, LocalDateTime startDateTime,
                                                           LocalDateTime endDateTime) throws DBException {
        int count;
        try {
            if (Params.FREE_PLACES_FIELD.equals(field)) {
                count = filmDao.findCountOfCurrentFilmsOrderByFreePlaces(startDateTime, endDateTime);
            } else {
                count = filmDao.findCountOfCurrentFilms(startDateTime, endDateTime);
            }
        } catch (SQLException | NamingException e) {
            String msg = "Getting all current films failed";
            log.error(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
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
            film = filmDao.findFilmById(id, connection);
            genres = genreDao.findGenresByFilmId(film.getId(), connection);
            connection.commit();

            filmInfo = new FilmInfo(film, genres);
        } catch (SQLException | NamingException e) {
            String msg = "Getting film failed";
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
        return filmInfo;
    }

}
