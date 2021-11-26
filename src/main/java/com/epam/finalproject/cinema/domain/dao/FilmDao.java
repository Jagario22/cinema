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
import java.util.ArrayList;
import java.util.List;

import static com.epam.finalproject.cinema.domain.constants.PostgresQuery.SELECT_ALL_CURRENT_FILMS;

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


    public List<Film> getAllCurrentFilms() throws SQLException, NamingException, IOException {
        List<Film> currentFilms = new ArrayList<>();
        Connection connection = null;
        Statement getAllSt = null;
        ResultSet resultSet = null;
        try {
            connection = connectionPool.getConnection();
            getAllSt = connection.createStatement();
            resultSet = getAllSt.executeQuery(SELECT_ALL_CURRENT_FILMS);
            while (resultSet.next()) {
                Film film = readFilm(resultSet);
                currentFilms.add(film);
            }
            connection.commit();
        } finally {
            CloseUtil.close(connection, getAllSt, resultSet);
        }

        return currentFilms;
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


    public void commit() throws SQLException, NamingException {
        connectionPool.getConnection().commit();;
    }
}