package com.epam.finalproject.cinema.service;

import com.epam.finalproject.cinema.domain.Genre.Genre;
import com.epam.finalproject.cinema.domain.Genre.GenreDao;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.film.Film;
import com.epam.finalproject.cinema.domain.film.FilmDao;
import com.epam.finalproject.cinema.domain.session.SessionDao;
import com.epam.finalproject.cinema.domain.session.SessionQuery;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.web.model.film.FilmInfo;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.epam.finalproject.cinema.domain.film.FilmQuery.FILM_TITLE_FIELD;
import static com.epam.finalproject.cinema.domain.session.SessionQuery.SESSIONS_DATE_TIME_FIELD;
import static com.epam.finalproject.cinema.web.constants.Params.DATE_TIME_FIELD;
import static com.epam.finalproject.cinema.web.constants.Params.TITLE_FIELD;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class FilmServiceTest {
    @Mock
    private Connection connection;

    @Mock
    private PostgresConnectionPool connectionPool;

    private FilmDao filmDao;

    private GenreDao genreDao;

    private TicketService ticketService;

    private SessionDao sessionDao;

    private FilmService filmService;

    @Before
    public void setUp() throws SQLException, NamingException {
        System.setProperty("logFile", "src\\test\\test_log.log");

        when(connectionPool.getConnection()).thenReturn(connection);
        try (MockedStatic<PostgresConnectionPool> theMock = Mockito.mockStatic(PostgresConnectionPool.class)) {
            theMock.when(PostgresConnectionPool::getInstance).thenReturn(connectionPool);
            filmService = FilmService.getInstance();
        }
        filmDao = Mockito.mock(FilmDao.class);
        genreDao = Mockito.mock(GenreDao.class);
        ticketService = Mockito.mock(TicketService.class);
        sessionDao = Mockito.mock(SessionDao.class);
        connection = connectionPool.getConnection();

        configFilmService();
    }

    private void configFilmService() {
        filmService.setFilmDao(filmDao);
        filmService.setTicketService(ticketService);
        filmService.setGenreDao(genreDao);
        filmService.setSessionDao(sessionDao);
        filmService.setConnectionPool(connectionPool);
    }

    @Test
    public void shouldReturnFilmInfoWhenGettingById() throws SQLException, DBException, NamingException {
        int id = 1;
        FilmInfo filmInfo = new FilmInfo(getFilmList().get(0), getGenreList());
        when(filmDao.findById(id, connectionPool.getConnection())).thenReturn(filmInfo.getFilm());
        when(genreDao.findGenresByFilmId(id, connectionPool.getConnection())).thenReturn(filmInfo.getGenres());
        assertEquals(filmInfo, filmService.getById(id));
    }

    @Test
    public void shouldReturnFilmInfoWhenGettingByDateTimeField() throws SQLException, DBException {
        LocalDateTime start = LocalDateTime.now();
        int offset = 0;
        int limit = 3;
        List<Film> films = getFilmList();
        when(filmDao.findAllCurrentOrderByField(SESSIONS_DATE_TIME_FIELD, start, null, offset, limit,
                connection)).thenReturn(films);
        assertEquals(films, filmService.getAllCurrentFilmsSortedByAndBetween(DATE_TIME_FIELD, start,
                null, offset, limit));
    }

    @Test
    public void shouldReturnFilmInfoWhenGettingByIdTitleField() throws SQLException, DBException {
        LocalDateTime start = LocalDateTime.now();
        int offset = 0;
        int limit = 3;
        List<Film> films = getFilmList();
        when(filmDao.findAllCurrentOrderByField(FILM_TITLE_FIELD, start, null, offset, limit,
                connection)).thenReturn(films);
        assertEquals(films, filmService.getAllCurrentFilmsSortedByAndBetween(TITLE_FIELD, start,
                null, offset, limit));
    }

    @Test
    public void shouldReturnFilmInfoWhenGettingByIdUnknownField() throws SQLException, DBException {
        LocalDateTime start = LocalDateTime.now();
        int offset = 0;
        int limit = 3;
        List<Film> films = getFilmList();
        when(filmDao.findAllCurrentOrderByField(SESSIONS_DATE_TIME_FIELD, start, null, offset, limit,
                connection)).thenReturn(films);
        assertEquals(films, filmService.getAllCurrentFilmsSortedByAndBetween("unknownField", start,
                null, offset, limit));
    }

    @Test(expected = DBException.class)
    public void shouldThrowDBExceptionWhenCatchSQLException() throws SQLException, DBException {
        LocalDateTime start = LocalDateTime.now();
        int offset = 0;
        int limit = 3;
        List<Film> films = getFilmList();
        when(filmDao.findAllCurrentOrderByField(FILM_TITLE_FIELD, start, null, offset, limit,
                connection)).thenThrow(new SQLException("Error Massage"));
        filmService.getAllCurrentFilmsSortedByAndBetween(TITLE_FIELD, start,
                null, offset, limit);
    }

    private List<Film> getFilmList() {
        List<Film> films = new ArrayList<>();
        films.add(new Film(1, "Title1", 168, "2021", 16, "this is description1",
                7.8F, "view/img1", Date.valueOf("2021-12-19")));
        films.add(new Film(2, "Title2", 158, "2021", 12, "this is description2",
                6.8F, "view/img2", Date.valueOf("2021-12-20")));
        films.add(new Film(3, "Title3", 120, "2021", 16, "this is description3",
                9, "view/img3", Date.valueOf("2021-12-21")));

        return films;
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
        Mockito.reset(filmDao, genreDao, sessionDao);
    }
}
