package com.epam.finalproject.movietheater.domain.dao;

import com.epam.finalproject.movietheater.domain.connection.ConnectionPool;
import com.epam.finalproject.movietheater.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.movietheater.domain.entity.Session;
import com.epam.finalproject.movietheater.domain.entity.Session.Lang;
import com.epam.finalproject.movietheater.domain.util.CloseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.epam.finalproject.movietheater.domain.constants.PostgresQuery.SELECT_ALL_CURRENT_SESSION_OF_FILM;

public class SessionDao {
    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(FilmDao.class);

    private SessionDao() {
        connectionPool = PostgresConnectionPool.getInstance();
        System.out.println();
    }

    private static SessionDao instance = null;

    public static synchronized SessionDao getInstance() {
        if (instance == null) {
            instance = new SessionDao();
        }
        return instance;
    }


    public  List<Session > findCurrentFilmSessionsByFilmId(int filmId) throws SQLException, NamingException {
        List<Session> currentSessions = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            ps = connection.prepareStatement(SELECT_ALL_CURRENT_SESSION_OF_FILM);
            ps.setInt(1, filmId);
            resultSet = ps.executeQuery();
            while (resultSet.next()) {
                Session session = readSession(resultSet);
                currentSessions.add(session);
            }
        } finally {
            CloseUtil.close(connection, ps, resultSet);
        }

        return currentSessions;
    }

    private Session readSession(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        int filmId = rs.getInt(2);
        LocalDateTime date = rs.getTimestamp(3).toLocalDateTime();
        Lang lang = Lang.valueOf(rs.getString(4).toUpperCase());
        return new Session(id, filmId, date, lang);
    }
}
