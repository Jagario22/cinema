package com.epam.finalproject.cinema.domain.film;

import com.epam.finalproject.cinema.util.CloseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.epam.finalproject.cinema.domain.film.FilmQuery.*;

/**
 * Manager which provides methods for operation with film entity in DB.
 * Works with PostgresSQL dialect
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class FilmDao {
    private final static Logger log = LogManager.getLogger(FilmDao.class);

    private FilmDao() {
    }

    private static FilmDao instance = null;

    public static synchronized FilmDao getInstance() {
        if (instance == null) {
            instance = new FilmDao();
        }
        return instance;
    }

    public int findCountOfAllCurrentOrderByFreePlaces(LocalDateTime start, LocalDateTime end, Connection connection) throws SQLException {
        return findCountOfAllCurrent(start, end, connection,
                SELECT_CURRENT_FILM_COUNT_ORDER_BY_PLACES_WHERE_DATETIME_AFTER,
                SELECT_CURRENT_FILM_COUNT_ORDER_BY_PLACES_WHERE_DATETIME_BETWEEN);
    }

    public int findCountOfAllCurrent(LocalDateTime start, LocalDateTime end, Connection connection) throws SQLException {
        return findCountOfAllCurrent(start, end, connection,
                SELECT_ALL_CURRENT_FILMS_COUNT_WHERE_DATETIME_AFTER,
                SELECT_ALL_CURRENT_FILMS_COUNT_WHERE_DATETIME_BETWEEN);
    }

    public int findCountOfAllCurrent(LocalDateTime start, LocalDateTime end, Connection connection,
                                     String SQL_WHERE_DATETIME_AFTER, String SQL_WHERE_DATETIME_BETWEEN) throws SQLException {
        int size;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            if (end == null) {
                ps = connection.prepareStatement(SQL_WHERE_DATETIME_AFTER);
                ps.setTimestamp(1, Timestamp.valueOf(start));
            } else {
                ps = connection.prepareStatement(SQL_WHERE_DATETIME_BETWEEN);
                ps.setTimestamp(1, Timestamp.valueOf(start));
                ps.setTimestamp(2, Timestamp.valueOf(end));
            }
            resultSet = ps.executeQuery();
            if (resultSet.next())
                size = resultSet.getInt(1);
            else {
                throw new SQLException("Can't find current films");
            }
            connection.commit();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            try {
                CloseUtil.close(ps, resultSet);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return size;
    }

    public List<Film> findAllCurrentOrderByField(String field, LocalDateTime start, LocalDateTime end,
                                                 int offset, int limit, Connection connection) throws SQLException {
        if (end == null) {
            String sql = String.format(SELECT_ALL_CURRENT_FILMS_WHERE_DATETIME_AFTER, field);
            return findAllCurrentWithDateTimeAfter(sql, start, offset, limit, connection);
        } else {
            String sql = String.format(SELECT_ALL_CURRENT_FILMS_WHERE_DATETIME_BETWEEN, field);
            return findAllCurrentWithDateTimeBetween(sql,
                    start, end, offset, limit, connection);
        }
    }

    public List<Film> findAllCurrentOrderByFreePlaces(LocalDateTime start, LocalDateTime end,
                                                      int offset, int limit, Connection connection) throws SQLException {
        if (end == null) {
            return findAllCurrentWithDateTimeAfter(SELECT_CURRENT_FILM_ORDER_BY_PLACES_WHERE_DATETIME_AFTER,
                    start, offset, limit, connection);
        } else {
            return findAllCurrentWithDateTimeBetween(SELECT_CURRENT_FILM_ORDER_BY_PLACES_WHERE_DATETIME_BETWEEN,
                    start, end, offset, limit, connection);
        }

    }

    private List<Film> findAllCurrentWithDateTimeAfter(String sql, LocalDateTime start,
                                                       int offset, int limit, Connection connection) throws SQLException {
        List<Film> currentFilms = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            ps = connection.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            rs = ps.executeQuery();

            while (rs.next()) {
                Film film = readFilm(rs);
                currentFilms.add(film);
            }

        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            try {
                CloseUtil.close(ps, rs);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        return currentFilms;
    }

    private List<Film> findAllCurrentWithDateTimeBetween(String sql, LocalDateTime start,
                                                         LocalDateTime end, int offset, int limit,
                                                         Connection connection) throws SQLException {
        List<Film> currentFilms = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
            ps.setInt(3, offset);
            ps.setInt(4, limit);
            rs = ps.executeQuery();

            while (rs.next()) {
                Film film = readFilm(rs);
                currentFilms.add(film);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            try {
                CloseUtil.close(ps, rs);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        return currentFilms;
    }

    public int insert(Film film, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int result;
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

            if (rs.next()) {
                result = rs.getInt(1);
            } else {
                throw new SQLException("Inserting film " + film + " failed");
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            try {
                CloseUtil.close(ps, rs);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return result;
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
            log.error(e.getMessage());
            throw e;
        } finally {
            try {
                CloseUtil.close(ps, rs);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
    }


    public Film findById(int id, Connection connection) throws SQLException {
        Film film = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(SELECT_FILM_BY_ID);
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
            try {
                CloseUtil.close(ps, resultSet);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return film;
    }

    public List<Film> findAll(Connection connection) throws SQLException {
        List<Film> films = new ArrayList<>();
        Statement st = null;
        ResultSet resultSet = null;
        try {
            st = connection.createStatement();
            resultSet = st.executeQuery(SELECT_ALL_FILMS);

            while (resultSet.next()) {
                Film film = readFilm(resultSet);
                films.add(film);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            try {
                CloseUtil.close(st, resultSet);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        return films;
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