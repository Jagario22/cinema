package com.epam.finalproject.cinema.domain.session;

import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.util.CloseUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class SessionDaoTest {
    private final static Logger log = Logger.getLogger(SessionDaoTest.class.getName());
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

    private SessionDao sessionDao;
    private List<Session> sessions;

    @Before
    public void setUp() throws SQLException, NamingException {
        System.setProperty("logFile", "src\\test\\test_log.log");
        when(connectionPool.getConnection()).thenReturn(connection);

        try (MockedStatic<PostgresConnectionPool> theMock = Mockito.mockStatic(PostgresConnectionPool.class)) {
            theMock.when(PostgresConnectionPool::getInstance).thenReturn(connectionPool);
            sessionDao = SessionDao.getInstance();
        }
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(any(String.class))).thenReturn(rs);
        when(connection.prepareStatement(any())).
                thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        setSessionList();
        connection = connectionPool.getConnection();
        sessionDao = SessionDao.getInstance();
    }

    @Test
    public void shouldDeleteById() throws SQLException {
        int id = 1;
        int size = sessions.size();
        mockDelete(id);
        sessionDao.deleteById(id, connection);
        assertEquals(size - 1, sessions.size());
    }

    @Test(expected = SQLException.class)
    public void shouldThrowExceptionIfNotDeleted() throws SQLException {
        int id = 1;
        when(ps.executeUpdate()).thenReturn(0);
        sessionDao.deleteById(id, connection);
    }

    @Test
    public void shouldInsertSessionAndReturnGeneratedKey() throws SQLException {
        int size = sessions.size();
        Session session = new Session(1, 3, LocalDateTime.of(2021, 12, 23, 9, 0),
                Session.Lang.UKRAINIAN);
        mockInsert(session);
        int generatedKey = size + 1;
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt(1)).thenReturn(generatedKey);
        assertEquals(generatedKey, sessionDao.insert(session, connection));
        assertEquals(size + 1, sessions.size());
    }

    @Test
    public void shouldReturnSessionWhenGettingById() throws SQLException {
        when(rs.next()).thenReturn(true).thenReturn(false);
        Session expectedSession = sessions.get(0);
        when(rs.next()).thenReturn(true).thenReturn(false);
        mockSession(expectedSession);
        assertEquals(expectedSession, sessionDao.findCurrentSessionById(any(Integer.class), connection));
    }

    @Test
    public void shouldReturnSessionWhenGettingByDatetime() throws SQLException {
        Session expectedSession = sessions.get(0);
        when(rs.next()).thenReturn(true).thenReturn(false);
        mockSession(expectedSession);
        assertEquals(expectedSession, sessionDao.findSessionByDatetime(Mockito.mock(LocalDateTime.class), connection));
    }

    @Test
    public void shouldReturnSessionWhenGettingByDatetimeBefore() throws SQLException {
        Session expectedSession = sessions.get(0);
        when(rs.next()).thenReturn(true).thenReturn(false);
        mockSession(expectedSession);
        assertEquals(expectedSession, sessionDao.findNearestSessionWhereDatetimeBefore(Mockito.mock(LocalDateTime.class),
                connection));
    }

    @Test
    public void shouldReturnSessionWhenGettingByDateAfter() throws SQLException {
        buildMock(sessions);
        assertEquals(sessions, sessionDao.findSessionsAfterDate(Mockito.mock(LocalDateTime.class), connection));
    }


    @Test
    public void shouldReturnMapOfIdAndCount() throws SQLException {
        Map<Integer, Integer> expectedMap = new HashMap<>();
        expectedMap.put(1, 1);
        expectedMap.put(2, 2);
        expectedMap.put(3, 3);

        index = -1;
        doAnswer(invocation -> {
            index++;
            if (index >= expectedMap.size())
                return false;
            int j = 0;
            for (Map.Entry<Integer, Integer> entry : expectedMap.entrySet()) {
                if (j == index) {
                    when(rs.getInt(1)).thenReturn(entry.getKey());
                    when(rs.getInt(2)).thenReturn(entry.getValue());
                }
                j++;
            }
            return true;
        }).when(rs).next();
        assertEquals(expectedMap, sessionDao.findTicketIdCountOfFilmGroupBySessionId(any(Integer.class), connection));
    }

    private void mockInsert(Session session) throws SQLException {
        when(connection.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        doAnswer(invocation -> {
            sessions.add(session);
            return true;
        }).when(ps).execute();
    }

    private void mockDelete(int index) throws SQLException {
        doAnswer(invocation -> {
            sessions.remove(sessions.get(index - 1));
            return index;
        }).when(ps).executeUpdate();
    }

    private void mockSession(Session session) throws SQLException {
        when(rs.getInt(1)).thenReturn(session.getId());
        when(rs.getInt(2)).thenReturn(session.getFilmId());
        when(rs.getTimestamp(3)).thenReturn(Timestamp.valueOf(session.getDateTime()));
        when(rs.getString(4)).thenReturn(session.getLang().name().toLowerCase());
    }

    public void buildMock(List<Session> sessionList) throws SQLException {
        index = -1;
        doAnswer(invocation -> {
            index++;
            if (index >= sessionList.size())
                return false;
            mockSession(sessionList.get(index));
            return true;
        }).when(rs).next();
    }


    private void setSessionList() {
        sessions = new ArrayList<>();
        sessions.add(new Session(1, 2, LocalDateTime.of(2021, 12, 22, 9, 0),
                Session.Lang.UKRAINIAN));
        sessions.add(new Session(2, 1, LocalDateTime.of(2021, 12, 12, 14, 0),
                Session.Lang.UKRAINIAN));
        sessions.add(new Session(3, 2, LocalDateTime.of(2021, 12, 22, 16, 5),
                Session.Lang.UKRAINIAN));
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
