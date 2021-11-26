package com.epam.finalproject.cinema.domain.dao;

import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.entity.Film;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class FilmDaoTest {
    private final static Logger log = Logger.getLogger(FilmDaoTest.class.getName());
    private int filmIndex;
    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement pstmt;

    @Mock
    private Statement stmt;

    @Mock
    private ResultSet rs;

    @Mock
    private PostgresConnectionPool connectionPool;

    private FilmDao filmDao;
    private List<Film> films;

    @Before
    public void setUp() throws SQLException, NamingException {
        films = new ArrayList<>();
        when(connectionPool.getConnection()).thenReturn(connection);

        try (MockedStatic<PostgresConnectionPool> theMock = Mockito.mockStatic(PostgresConnectionPool.class)) {
            theMock.when(PostgresConnectionPool::getInstance).thenReturn(connectionPool);
            filmDao = FilmDao.getInstance();
        }

        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(any(String.class))).thenReturn(rs);
        films.add(new Film(1, "Title1", 168, "2021", 16, "this is description1", 7.8F,
                "view/img1", Date.valueOf("2021-12-19")));
        films.add(new Film(2, "Title2", 158, "2021", 12, "this is description2", 6.8F, "view/img2",
                Date.valueOf("2021-12-20")));
        films.add(new Film(3, "Title3", 120, "2021", 16, "this is description3", 9, "view/img3",
                Date.valueOf("2021-12-21")));

        filmIndex = -1;
    }


    @Test
    public void getCurrentFilms() {
        List<Film> expectedFilms = films;
        try {
            buildMock(expectedFilms);
            assertEquals(expectedFilms, filmDao.getAllCurrentFilms());
        } catch (SQLException | NamingException | IOException e) {
            log.severe(e.getMessage());
        }
    }

    @Test
    public void getEmptyResultOfCurrentFields() {
        try {
            when(rs.next()).thenReturn(false);
            assertEquals(0, filmDao.getAllCurrentFilms().size());
        } catch (SQLException | NamingException | IOException e) {
            log.severe(e.getMessage());
        }
    }

    private void MockFilm(Film film) throws SQLException {
        when(rs.getInt(1)).thenReturn(film.getId());
        when(rs.getString(2)).thenReturn(film.getTitle());
        when(rs.getInt(3)).thenReturn(film.getLen());
        when(rs.getString(4)).thenReturn(film.getYear());
        when(rs.getInt(5)).thenReturn(film.getCategory());
        when(rs.getString(6)).thenReturn(film.getDescr());
        when(rs.getFloat(7)).thenReturn(film.getRating());
        when(rs.getString(8)).thenReturn(film.getImg());
        when(rs.getDate(9)).thenReturn(film.getLastShowingDate());
    }

    public void buildMock(List<Film> films) throws SQLException {
        doAnswer(invocation -> {
            filmIndex++;
            if (filmIndex >= films.size())
                return false;
            MockFilm(films.get(filmIndex));
            return true;
        }).when(rs).next();
    }

    @After
    public void afterEach() {
        filmIndex = 0;
    }
}
