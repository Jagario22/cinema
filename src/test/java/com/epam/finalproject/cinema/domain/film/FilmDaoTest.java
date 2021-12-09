package com.epam.finalproject.cinema.domain.film;

import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
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
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class FilmDaoTest {
    private final static Logger log = Logger.getLogger(FilmDaoTest.class.getName());
    private int index;
    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement ps;

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
        System.setProperty("logFile", "src\\test\\test_log.log");
        films = new ArrayList<>();
        when(connectionPool.getConnection()).thenReturn(connection);

        try (MockedStatic<PostgresConnectionPool> theMock = Mockito.mockStatic(PostgresConnectionPool.class)) {
            theMock.when(PostgresConnectionPool::getInstance).thenReturn(connectionPool);
            filmDao = FilmDao.getInstance();
        }
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(any(String.class))).thenReturn(rs);
        when(connection.prepareStatement(any())).
                thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        setFilmList();
        connection = connectionPool.getConnection();
        filmDao = FilmDao.getInstance();
    }

    private void mockCount(int countOfFilms) throws SQLException {
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt(1)).thenReturn(countOfFilms);
    }

    @Test
    public void shouldReturnCountOf() throws SQLException {
        int countOfFilms = films.size();
        mockCount(countOfFilms);
        assertEquals(countOfFilms, filmDao.findCountOfAllCurrentOrderByFreePlaces(LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),
                connection));
        mockCount(countOfFilms);
        assertEquals(countOfFilms, filmDao.findCountOfAllCurrentOrderByFreePlaces(LocalDateTime.now(), null,
                connection));
    }

    @Test
    public void shouldReturnZeroCount() throws SQLException {
        mockCount(0);
        assertEquals(0, filmDao.findCountOfAllCurrentOrderByFreePlaces(LocalDateTime.now(), null,
                connection));
        mockCount(0);
        assertEquals(0, filmDao.findCountOfAllCurrentOrderByFreePlaces(LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),
                connection));
    }


    @Test
    public void shouldReturnCurrentFilms() throws SQLException {
        buildMock(films);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(1);
        assertEquals(films, filmDao.findAllCurrentOrderByFreePlaces(start,
                null, 1, 3,
                connection));
        buildMock(films);
        assertEquals(films, filmDao.findAllCurrentOrderByFreePlaces(start,
                end, 1, 3,
                connection));
        buildMock(films);
        String field = FilmQuery.FILM_TITLE_FIELD;
        assertEquals(films, filmDao.findAllCurrentOrderByField(field, start,
                null, 1, 3, connection));
        buildMock(films);
        assertEquals(films, filmDao.findAllCurrentOrderByField(field, start,
                end, 1, 3, connection));


    }

    @Test
    public void shouldReturnEmptyListWhenThereAreNoFilms() throws SQLException {
        when(rs.next()).thenReturn(false);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusMinutes(1);
        List<Film> emptyFilms = new ArrayList<>();

        assertEquals(emptyFilms, filmDao.findAllCurrentOrderByFreePlaces(start,
                null, 1, 3,
                connection));
        assertEquals(emptyFilms, filmDao.findAllCurrentOrderByFreePlaces(start,
                end, 1, 3,
                connection));

        String field = FilmQuery.FILM_TITLE_FIELD;
        assertEquals(emptyFilms, filmDao.findAllCurrentOrderByField(field, start,
                null, 1, 3, connection));
        assertEquals(emptyFilms, filmDao.findAllCurrentOrderByField(field, start,
                end, 1, 3, connection));
    }

    @Test
    public void shouldInsertFilmThenReturnGeneratedKey() throws SQLException {
        int generatedId = 1;
        mockInsert();
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt(1)).thenReturn(generatedId);
        assertEquals(generatedId, filmDao.insert(films.get(0), connection));

    }

    @Test
    public void shouldInsertGenreOfFilmThenReturnGeneratedKey() throws SQLException {
        mockInsert();
        when(rs.next()).thenReturn(true).thenReturn(false);
        filmDao.insertGenreOfFilm(1, 1, connection);
        assertTrue(true);

    }

    @Test(expected = SQLException.class)
    public void shouldThrowSQLExceptionWhenInsertFilm() throws SQLException {
        mockInsert();
        when(rs.next()).thenReturn(false);
        filmDao.insert(films.get(0), connection);
    }

    @Test(expected = SQLException.class)
    public void shouldThrowSQLExceptionWhenInsertGenreOfFilm() throws SQLException {
        mockInsert();
        when(rs.next()).thenReturn(false);
        filmDao.insertGenreOfFilm(1, 2, connection);
    }

    @Test
    public void shouldReturnFilmById() throws SQLException {
        Film expectedFilm = films.get(0);
        when(rs.next()).thenReturn(true).thenReturn(false);
        mockFilm(expectedFilm);
        assertEquals(expectedFilm, filmDao.findById(1, connection));
    }

    @Test(expected = SQLException.class)
    public void shouldThrowSQLExceptionIfFilmByIdNotFound() throws SQLException {
        when(rs.next()).thenReturn(false);
        assertNull(filmDao.findById(1, connection));
    }

    private void mockInsert() throws SQLException {
        when(connection.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(rs);
    }

    private void mockFilm(Film film) throws SQLException {
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
        index = -1;
        doAnswer(invocation -> {
            index++;
            if (index >= films.size())
                return false;
            mockFilm(films.get(index));
            return true;
        }).when(rs).next();
    }

    private void setFilmList() {
        films.add(new Film(1, "Title1", 168, "2021", 16, "this is description1",
                7.8F, "view/img1", Date.valueOf("2021-12-19")));
        films.add(new Film(2, "Title2", 158, "2021", 12, "this is description2",
                6.8F, "view/img2", Date.valueOf("2021-12-20")));
        films.add(new Film(3, "Title3", 120, "2021", 16, "this is description3",
                9, "view/img3", Date.valueOf("2021-12-21")));
    }


    @After
    public void afterEach() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.severe(e.getMessage());
        }
        Mockito.reset(stmt, ps, rs, connectionPool, connection);
    }
}
