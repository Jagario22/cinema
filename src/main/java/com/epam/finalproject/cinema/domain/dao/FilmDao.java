package com.epam.finalproject.cinema.domain.dao;


import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.constants.PostgresQuery;
import com.epam.finalproject.cinema.domain.entity.Film;
import com.epam.finalproject.cinema.util.CloseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.epam.finalproject.cinema.domain.constants.PostgresQuery.*;

public class FilmDao {
    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(FilmDao.class);

    private FilmDao() {
        connectionPool = PostgresConnectionPool.getInstance();
    }

    private static FilmDao instance = null;

    public static synchronized FilmDao getInstance() {
        if (instance == null) {
            instance = new FilmDao();
        }
        return instance;
    }

    public int findCountOfCurrentFilmsOrderByFreePlaces(LocalDateTime start, LocalDateTime end) throws SQLException, NamingException {
        int size;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            if (end == null) {
                ps = connection.prepareStatement(SELECT_CURRENT_FILM_COUNT_ORDER_BY_PLACES_WHERE_DATETIME_AFTER);
                ps.setTimestamp(1, Timestamp.valueOf(start));
            } else {
                ps = connection.prepareStatement(SELECT_CURRENT_FILM_COUNT_ORDER_BY_PLACES_WHERE_DATETIME_BETWEEN);
                ps.setTimestamp(1, Timestamp.valueOf(start));
                ps.setTimestamp(2,  Timestamp.valueOf(end));

            }
            resultSet = ps.executeQuery();
            if (resultSet.next())
                size = resultSet.getInt(1);
            else {
                connection.rollback();
                throw new SQLException("Can't find current films");
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            CloseUtil.close(connection, ps, resultSet);
        }
        return size;
    }


    public int findCountOfCurrentFilms(LocalDateTime start, LocalDateTime end) throws SQLException, NamingException {
        int size;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            if (end == null) {
                ps = connection.prepareStatement(SELECT_ALL_CURRENT_FILMS_COUNT_WHERE_DATETIME_AFTER);
                ps.setTimestamp(1, Timestamp.valueOf(start));
            } else {
                ps = connection.prepareStatement(SELECT_ALL_CURRENT_FILMS_COUNT_WHERE_DATETIME_BETWEEN);
                ps.setTimestamp(1, Timestamp.valueOf(start));
                ps.setTimestamp(2,  Timestamp.valueOf(end));

            }
            resultSet = ps.executeQuery();
            if (resultSet.next())
                size = resultSet.getInt(1);
            else {
                connection.rollback();
                throw new SQLException("Can't find current films");
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            CloseUtil.close(connection, ps, resultSet);
        }
        return size;
    }

    public List<Film> getAllCurrentFilmsOrderBy(String field, LocalDateTime start, LocalDateTime end, int offset, int limit) throws SQLException, NamingException {
        if (end == null) {
            String sql = String.format(SELECT_ALL_CURRENT_FILMS_WHERE_DATETIME_AFTER, field);
            return getAllCurrentFilmsWhereDateTimeAfter(sql, start, offset, limit);
        } else {
            String sql = String.format(SELECT_ALL_CURRENT_FILMS_WHERE_DATETIME_BETWEEN, field);
            return getAllCurrentFilmsWhereDateTimeBetween(sql,
                    start, end, offset, limit);
        }
    }

    public List<Film> getAllCurrentFilmsOrderByPlaces(LocalDateTime start, LocalDateTime end, int offset, int limit) throws SQLException, NamingException, IOException {
        if (end == null) {
            return getAllCurrentFilmsWhereDateTimeAfter(SELECT_CURRENT_FILM_ORDER_BY_PLACES_WHERE_DATETIME_AFTER,
                    start, offset, limit);
        } else {
            return getAllCurrentFilmsWhereDateTimeBetween(SELECT_CURRENT_FILM_ORDER_BY_PLACES_WHERE_DATETIME_BETWEEN,
                    start, end, offset, limit);
        }

    }


    private List<Film> getAllCurrentFilmsWhereDateTimeAfter(String sql, LocalDateTime start, int offset, int limit) throws SQLException, NamingException {
        List<Film> currentFilms = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Film film = readFilm(resultSet);
                currentFilms.add(film);
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            CloseUtil.close(connection, ps, resultSet);
        }

        return currentFilms;
    }

    private List<Film> getAllCurrentFilmsWhereDateTimeBetween(String sql, LocalDateTime start, LocalDateTime end, int offset, int limit) throws SQLException, NamingException {
        List<Film> currentFilms = new ArrayList<>();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            ps = connection.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ps.setInt(3, offset);
            ps.setInt(4, limit);
            resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Film film = readFilm(resultSet);
                currentFilms.add(film);
            }
            connection.commit();
        } catch (SQLException | NamingException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            CloseUtil.close(connection, ps, resultSet);
        }

        return currentFilms;
    }

    public void insert(Film film, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(INSERT_INTO_FILMS_VALUES, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getTitle());
            ps.setInt(2, film.getLen());
            ps.setString(3, film.getYear());
            ps.setInt(4, film.getCategory());
            ps.setString(5, film.getDescr());
            ps.setFloat(6, film.getRating());
            ps.setString(7, film.getImg());
            ps.setDate(8, film.getLastShowingDate());

            ps.execute();

            rs = ps.getGeneratedKeys();

            if (!rs.next()) {
                throw new SQLException("Inserting film " + film + " failed");
            }
        } catch (SQLException e) {
            log.error("Inserting film " + film + " failed");
            throw e;
        } finally {
            try {
                CloseUtil.close(ps, rs);
            } catch (SQLException e) {
                log.error("Inserting film " + film + " failed");
            }
        }

    }


    public void insertGenreOfFilm(int genresId, int filmId, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(INSERT_INTO_GENRES_FILMS, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, filmId);
            ps.setInt(2, genresId);
            ps.execute();
            rs = ps.getGeneratedKeys();

            if (!rs.next()) {
                throw new SQLException("Assigning a genre with id: " + genresId + " to a film with id: " + filmId +
                        " failed");
            }
        } catch (SQLException e) {
            log.error("Assigning a genre with id: " + genresId + " to a film with id: " + filmId +
                    " failed");
            throw e;
        } finally {
            try {
                CloseUtil.close(ps, rs);
            } catch (SQLException e) {
                log.error("Assigning a genre with id: " + genresId + " to a film with id: " + filmId +
                        " failed");
            }
        }
    }


    public Film findFilmById(int id, Connection connection) throws SQLException {
        Film film = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(PostgresQuery.SELECT_FILM_BY_ID);
            ps.setInt(1, id);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                film = readFilm(resultSet);
            } else {
                String msg = "Film by id: " + id + " wasn't found";
                log.error(msg);
                throw new SQLException(msg);
            }
        } finally {
            CloseUtil.close(ps, resultSet);
        }
        return film;
    }

    private Film readFilm(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt(1);
        String title = resultSet.getString(2);
        int len = resultSet.getInt(3);
        String year_prod = resultSet.getString(4);
        int category = resultSet.getInt(5);
        String descr = resultSet.getString(6);
        float rating = resultSet.getFloat(7);
        String img = resultSet.getString(8);
        Date lastShowingDate = resultSet.getDate(9);
        return new Film(id, title, len, year_prod, category, descr, rating, img, lastShowingDate);
    }





}