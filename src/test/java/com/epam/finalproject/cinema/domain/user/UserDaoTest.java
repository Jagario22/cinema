package com.epam.finalproject.cinema.domain.user;

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

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UserDaoTest {
    private final static Logger log = Logger.getLogger(UserDao.class.getName());
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

    private UserDao userDao;
    private List<User> users;

    @Before
    public void setUp() throws SQLException, NamingException {
        System.setProperty("logFile", "src\\test\\test_log.log");
        when(connectionPool.getConnection()).thenReturn(connection);

        try (MockedStatic<PostgresConnectionPool> theMock = Mockito.mockStatic(PostgresConnectionPool.class)) {
            theMock.when(PostgresConnectionPool::getInstance).thenReturn(connectionPool);
            userDao = UserDao.getInstance();
        }
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(any(String.class))).thenReturn(rs);
        when(connection.prepareStatement(any())).
                thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        setSessionList();
        connection = connectionPool.getConnection();
        userDao = UserDao.getInstance();
    }


    @Test
    public void shouldReturnUserWhenGettingByLoginAndPassword() throws SQLException {
        String login = "login";
        String password = "password";
        User user = users.get(0);
        when(rs.next()).thenReturn(true);
        mockUser(user);
        assertEquals(user, userDao.findUserByLoginAndPassword(login, password, connection));
    }

    @Test(expected = SQLException.class)
    public void shouldReturnExceptionWhenGettingUserFailed() throws SQLException {
        when(rs.next()).thenReturn(false);
        userDao.findUsersWithEqualLoginOrEmail("login", "password", connection);
    }

    @Test
    public void shouldReturnUserWhenGettingWithEqualLoginOrEmail() throws SQLException {
        index = -1;
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(1);
        when(rs.getInt(2)).thenReturn(0);
        when(rs.getInt(3)).thenReturn(0);
        String login = "login";
        User user = new User(login);
        assertEquals(user.getLogin(), userDao.findUsersWithEqualLoginOrEmail(login, "email", connection)
                .get(0).getLogin());
    }

    @Test
    public void shouldInsertUserThenReturnGeneratedKey() throws SQLException {
        int size = users.size();
        User newUser = new User(size + 1, "newEmail","newLogin", "newPassword", User.ROLE.USER);
        mockInsert(newUser);
        int generatedKey = size + 1;
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt(1)).thenReturn(generatedKey);
        assertEquals(generatedKey, userDao.insert(newUser, connection));
        assertEquals(size + 1, users.size());
    }

    private void setSessionList() {
        users = new ArrayList<>();
        users.add(new User(1, "user1@gmail.com", "Password1&", "user1", User.ROLE.USER));
        users.add(new User(2, "user2@gmail.com", "Password1&", "user2", User.ROLE.USER));
        users.add(new User(3, "user3@gmail.com", "Password1&", "user3", User.ROLE.ADMIN));
    }

    private void mockInsert(User user) throws SQLException {
        when(connection.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        doAnswer(invocation -> {
            users.add(user);
            return true;
        }).when(ps).execute();
    }

    private void mockUser(User user) throws SQLException {
        when(rs.getInt(1)).thenReturn(user.getId());
        when(rs.getString(2)).thenReturn(user.getEmail());
        when(rs.getString(3)).thenReturn(user.getLogin());
        when(rs.getString(4)).thenReturn(user.getPassword());
        when(rs.getString(5)).thenReturn(user.getRole().name().toLowerCase());
    }

    public void buildMock(List<User> userList) throws SQLException {
        index = -1;
        doAnswer(invocation -> {
            index++;
            if (index >= userList.size())
                return false;
            mockUser(userList.get(index));
            return true;
        }).when(rs).next();
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
