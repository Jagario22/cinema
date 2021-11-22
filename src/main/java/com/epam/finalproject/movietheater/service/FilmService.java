package com.epam.finalproject.movietheater.service;

import com.epam.finalproject.movietheater.domain.connection.ConnectionPool;
import com.epam.finalproject.movietheater.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.movietheater.domain.dao.FilmDao;
import com.epam.finalproject.movietheater.domain.dao.GenreDao;
import com.epam.finalproject.movietheater.domain.entity.Film;
import com.epam.finalproject.movietheater.domain.entity.Genre;
import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.web.model.FilmDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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

    public synchronized List<Film> getAllCurrentFilms() throws DBException {
        List<Film> currentFilms;
        try {
            currentFilms = filmDao.getAllCurrentFilms();
        } catch (SQLException | NamingException | IOException e) {
            String msg = "Getting all current films failed";
            log.error(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
        }
        return currentFilms;
    }

    public FilmDTO getById(int id) throws DBException {
        Film film;
        List<Genre> genres;
        FilmDTO filmDTO;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            film = filmDao.findFilmById(id, connection);
            genres = genreDao.findGenresByFilmId(film.getId(), connection);
            connection.commit();

            filmDTO = new FilmDTO(film, genres);
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
        return filmDTO;
    }



}
