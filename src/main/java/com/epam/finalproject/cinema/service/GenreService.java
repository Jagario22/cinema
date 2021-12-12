package com.epam.finalproject.cinema.service;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.Genre.GenreDao;
import com.epam.finalproject.cinema.domain.Genre.Genre;
import com.epam.finalproject.cinema.exception.DBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GenreService {
    private static GenreService instance = null;
    private GenreDao genreDao = GenreDao.getInstance();
    private ConnectionPool connectionPool = PostgresConnectionPool.getInstance();
    private final static Logger log = LogManager.getLogger(GenreService.class);

    public static synchronized GenreService getInstance() {
        if (instance == null) {
            instance = new GenreService();
        }
        return instance;
    }

    public List<Genre> getAllGenres() throws DBException {
        Connection connection = null;
        List<Genre> genres;
        try {
            connection = connectionPool.getConnection();
            genres = genreDao.findAll(connection);
            connection.commit();
        } catch (SQLException | NamingException e) {
            String errorMsg = "Getting all genres failed";
            connectionRollback(connection, errorMsg);
            log.debug("Getting all genres failed");
            throw new DBException(errorMsg, e);
        } finally {
            connectionClose(connection, "Getting all genres failed");
        }

        return genres;
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

    public void setGenreDao(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public void setConnectionPool(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }
}
