package com.epam.finalproject.cinema.domain.dao;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.entity.Film;
import com.epam.finalproject.cinema.domain.entity.Genre;
import com.epam.finalproject.cinema.util.CloseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.epam.finalproject.cinema.domain.constants.PostgresQuery.*;

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

    public void insert(Genre genre, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(INSERT_INTO_GENRES_VALUES, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, genre.getName());
            ps.execute();
            rs = ps.getGeneratedKeys();

            if (!rs.next()) {
                throw new SQLException("Inserting genre " + genre + " failed");
            }
        } catch (SQLException e) {
            log.error("Inserting genre " + genre + " failed");
            throw e;
        } finally {
            try {
                CloseUtil.close(ps, rs);
            } catch (SQLException e) {
                log.error("Inserting genre " + genre + " failed");
            }
        }

    }

    public List<Genre> findGenresByFilmId(int filmId, Connection connection) throws SQLException, NamingException {
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

    public List<Genre> findAll(Connection connection) throws SQLException, NamingException {
        List<Genre> genres = new ArrayList<>();
        Statement st = null;
        ResultSet resultSet = null;

        try {

            st = connection.createStatement();
            resultSet = st.executeQuery(SELECT_ALL_GENRES);
            while (resultSet.next()) {
                Genre genre = readGenre(resultSet);
                genres.add(genre);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        }

        return genres;
    }

    private Genre readGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String name = rs.getString(2);
        return new Genre(id, name);
    }


}
