package com.epam.finalproject.cinema.domain.genre;

import com.epam.finalproject.cinema.domain.Genre.Genre;
import com.epam.finalproject.cinema.domain.Genre.GenreDao;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class GenreDaoTest {
    private final static Logger log = Logger.getLogger(GenreDaoTest.class.getName());
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

    private GenreDao genreDao;
    private List<Genre> genres;

    @Before
    public void setUp() throws SQLException, NamingException {
        System.setProperty("logFile", "src\\test\\test_log.log");
        when(connectionPool.getConnection()).thenReturn(connection);

        try (MockedStatic<PostgresConnectionPool> theMock = Mockito.mockStatic(PostgresConnectionPool.class)) {
            theMock.when(PostgresConnectionPool::getInstance).thenReturn(connectionPool);
            genreDao = GenreDao.getInstance();
        }
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(any(String.class))).thenReturn(rs);
        when(connection.prepareStatement(any())).
                thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        setGenreList();
        connection = connectionPool.getConnection();
        genreDao = GenreDao.getInstance();
    }

    @Test
    public void shouldInsertGenreThenReturnGeneratedKey() throws SQLException {
        int size = genres.size();
        Genre newGenre = new Genre(size, "comedy");
        mockInsert(newGenre);
        int generatedKey = size + 1;
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt(1)).thenReturn(generatedKey);
        assertEquals(generatedKey, genreDao.insert(newGenre, connection));
        assertEquals(size + 1, genres.size());
    }


    @Test
    public void shouldReturnListOfGenres() throws SQLException {
        buildMock(genres);
        assertEquals(genres, genreDao.findGenresByFilmId(1, connection));
        buildMock(genres);
        assertEquals(genres, genreDao.findAll(connection));
    }

    @Test
    public void shouldReturnEmptyListIfGenresNotFound() throws SQLException {
        when(rs.next()).thenReturn(false);
        List<Genre> emptyList = new ArrayList<>();
        assertEquals(emptyList, genreDao.findGenresByFilmId(1, connection));
        assertEquals(emptyList, genreDao.findAll(connection));
    }

    private void mockInsert(Genre genre) throws SQLException {
        when(connection.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        doAnswer(invocation -> {
            genres.add(genre);
            return true;
        }).when(ps).execute();
    }

    private void mockGenre(Genre genre) throws SQLException {
        when(rs.getInt(1)).thenReturn(genre.getId());
        when(rs.getString(2)).thenReturn(genre.getName());
    }

    public void buildMock(List<Genre> genresList) throws SQLException {
        index = -1;
        doAnswer(invocation -> {
            index++;
            if (index >= genresList.size())
                return false;
            mockGenre(genresList.get(index));
            return true;
        }).when(rs).next();
    }

    private void setGenreList() {
        genres = new ArrayList<>();
        genres.add(new Genre(1, "action"));
        genres.add(new Genre(1, "drama"));
        genres.add(new Genre(1, "thriller"));
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