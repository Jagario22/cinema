package com.epam.finalproject.cinema.service;

import com.epam.finalproject.cinema.domain.Genre.Genre;
import com.epam.finalproject.cinema.domain.Genre.GenreDao;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.film.Film;
import com.epam.finalproject.cinema.domain.film.FilmDao;
import com.epam.finalproject.cinema.domain.session.SessionDao;
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
import java.util.ArrayList;
import java.util.List;

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

        filmDao = Mockito.mock(FilmDao.class);
        genreDao = Mockito.mock(GenreDao.class);
        ticketService = Mockito.mock(TicketService.class);
        sessionDao = Mockito.mock(SessionDao.class);
        mockInit();
        connection = connectionPool.getConnection();
    }

    @Test
    public void shouldReturnFilmInfoWhenGettingById() throws SQLException, DBException {
        int id = 1;
        FilmInfo filmInfo = new FilmInfo(getFilmList().get(0), getGenreList());
        when(filmDao.findById(id, connection)).thenReturn(filmInfo.getFilm());
        when(genreDao.findGenresByFilmId(id, connection)).thenReturn(filmInfo.getGenres());
        assertEquals(filmInfo, filmService.getById(id));
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


    private void mockInit() {
        try (MockedStatic<PostgresConnectionPool> theMock = Mockito.mockStatic(PostgresConnectionPool.class)) {
            theMock.when(PostgresConnectionPool::getInstance).thenReturn(connectionPool);
            try (MockedStatic<FilmDao> filmDaoMockedStatic = Mockito.mockStatic(FilmDao.class)) {
                filmDaoMockedStatic.when(FilmDao::getInstance).thenReturn(filmDao);
                try (MockedStatic<GenreDao> genreDaoMockedStatic = Mockito.mockStatic(GenreDao.class)) {
                    genreDaoMockedStatic.when(GenreDao::getInstance).thenReturn(genreDao);
                    try (MockedStatic<TicketService> ticketServiceMockedStatic = Mockito.mockStatic(TicketService.class)) {
                        ticketServiceMockedStatic.when(TicketService::getInstance).thenReturn(ticketService);
                        try (MockedStatic<SessionDao> sessionDaoMockedStatic = Mockito.mockStatic(SessionDao.class)) {
                            sessionDaoMockedStatic.when(SessionDao::getInstance).thenReturn(sessionDao);
                            filmService = FilmService.getInstance();
                        }
                    }
                }
            }
        }
    }

    @After
    public void afterEach() throws SQLException {
        connection.close();
        Mockito.reset(filmDao, genreDao, sessionDao);
    }
}
