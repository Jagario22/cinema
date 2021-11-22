package com.epam.finalproject.movietheater.domain.dao;

import com.epam.finalproject.movietheater.domain.connection.ConnectionPool;
import com.epam.finalproject.movietheater.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.movietheater.domain.entity.Genre;
import com.epam.finalproject.movietheater.domain.util.CloseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.epam.finalproject.movietheater.domain.constants.PostgresQuery.SELECT_ALL_GENRES_OF_FILM;

public class GenreDao {
    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(FilmDao.class);

    private GenreDao() {
        connectionPool = PostgresConnectionPool.getInstance();
    }

    private static GenreDao instance = null;

    public static synchronized GenreDao getInstance() {
        if (instance == null) {
            instance = new GenreDao();
        }
        return instance;
    }

    public  List<Genre> findGenresByFilmId(int filmId, Connection connection) throws SQLException, NamingException {
        List<Genre> genres = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet resultSet = null;

        try {
            ps = connection.prepareStatement(SELECT_ALL_GENRES_OF_FILM);
            ps.setInt(1, filmId);
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Genre genre = readGenre(resultSet);
                genres.add(genre);
            }
        } finally {
            CloseUtil.close(ps, resultSet);
        }

        return genres;
    }

    private Genre readGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String name = rs.getString(2);
        return new Genre(id, name);
    }
}
