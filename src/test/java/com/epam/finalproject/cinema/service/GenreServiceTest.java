package com.epam.finalproject.cinema.service;

import com.epam.finalproject.cinema.domain.Genre.Genre;
import com.epam.finalproject.cinema.domain.Genre.GenreDao;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.exception.DBException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class GenreServiceTest {
    @Mock
    private Connection connection;
    @Mock
    private PostgresConnectionPool connectionPool;

    private GenreDao genreDao;

    private GenreService genreService;

    @Before
    public void setUp() throws SQLException, NamingException {
        System.setProperty("logFile", "src\\test\\test_log.log");

        when(connectionPool.getConnection()).thenReturn(connection);
        try (MockedStatic<PostgresConnectionPool> theMock = Mockito.mockStatic(PostgresConnectionPool.class)) {
            theMock.when(PostgresConnectionPool::getInstance).thenReturn(connectionPool);
            genreService = GenreService.getInstance();
        }

        genreDao = Mockito.mock(GenreDao.class);
        connection = connectionPool.getConnection();

        configFilmService();
    }

    private void configFilmService() {
        genreService.setGenreDao(genreDao);
        genreService.setConnectionPool(connectionPool);
    }

    @Test
    public void shouldReturnGenres() throws SQLException, DBException {
        List<Genre> genres = getGenreList();

        when(genreDao.findAll(connection)).thenReturn(genres);
        assertEquals(genres, genreService.getAllGenres());
    }

    @Test(expected = DBException.class)
    public void shouldThrowDBExceptionWhenCatchSQLException() throws SQLException, DBException {
        when(genreDao.findAll(connection)).thenThrow(new SQLException("Error message"));
        genreService.getAllGenres();
    }

    private List<Genre> getGenreList() {
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre(1, "action"));
        genres.add(new Genre(1, "drama"));
        genres.add(new Genre(1, "thriller"));
        return genres;
    }

    @After
    public void afterEach() {
        Mockito.reset(genreDao);
    }
}
