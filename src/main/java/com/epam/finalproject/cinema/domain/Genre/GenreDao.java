package com.epam.finalproject.cinema.domain.Genre;

import com.epam.finalproject.cinema.domain.film.FilmDao;
import com.epam.finalproject.cinema.util.CloseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.epam.finalproject.cinema.domain.Genre.GenreQuery.*;

public class GenreDao {
    private final static Logger log = LogManager.getLogger(FilmDao.class);

    private GenreDao() {

    }

    private static GenreDao instance = null;

    public static synchronized GenreDao getInstance() {
        if (instance == null) {
            instance = new GenreDao();
        }
        return instance;
    }

    public int insert(Genre genre, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int result;
        try {
            ps = connection.prepareStatement(INSERT_INTO_GENRES_VALUES, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, genre.getName());
            ps.execute();
            rs = ps.getGeneratedKeys();

            if (!rs.next()) {
                throw new SQLException("Inserting genre " + genre + " failed");
            }

            result = rs.getInt(1);
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            CloseUtil.close(ps, rs);
        }
        return result;
    }

    public List<Genre> findGenresByFilmId(int filmId, Connection connection) throws SQLException {
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

    public List<Genre> findAll(Connection connection) throws SQLException {
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
        } finally {
            CloseUtil.close(st, resultSet);
        }

        return genres;
    }

    private Genre readGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String name = rs.getString(2);
        return new Genre(id, name);
    }
}
