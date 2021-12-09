package com.epam.finalproject.cinema.domain.ticket;

import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.ticket.type.TicketType;
import com.epam.finalproject.cinema.domain.ticket.type.TicketTypeDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.naming.NamingException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TicketTypeDaoTest {
    private final static Logger log = Logger.getLogger(TicketTypeDaoTest.class.getName());
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


    private TicketTypeDao ticketTypeDao;
    private List<TicketType> ticketTypes;

    @Before
    public void setUp() throws SQLException, NamingException {
        System.setProperty("logFile", "src\\test\\test_log.log");
        when(connectionPool.getConnection()).thenReturn(connection);

        try (MockedStatic<PostgresConnectionPool> theMock = Mockito.mockStatic(PostgresConnectionPool.class)) {
            theMock.when(PostgresConnectionPool::getInstance).thenReturn(connectionPool);
            ticketTypeDao = TicketTypeDao.getInstance();
        }
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(any(String.class))).thenReturn(rs);
        when(connection.prepareStatement(any())).
                thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        setSessionList();
        connection = connectionPool.getConnection();
        ticketTypeDao = TicketTypeDao.getInstance();
    }

    @Test
    public void shouldInsertAndReturnGeneratedKey() throws SQLException {
        int size = ticketTypes.size();
        TicketType newTicketType = new TicketType(size + 1, "holiday", new BigDecimal(300));
        mockInsert(newTicketType);
        int generatedKey = size + 1;
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt(1)).thenReturn(generatedKey);
        assertEquals(generatedKey, ticketTypeDao.insert(newTicketType, connection));
        assertEquals(size + 1, ticketTypes.size());
    }

    @Test
    public void shouldReturnTicketTypeWhenGettingById() throws SQLException {
        TicketType ticketType = ticketTypes.get(0);
        when(rs.next()).thenReturn(true);
        mockTicketType(ticketType);
        assertEquals(ticketType, ticketTypeDao.findById(any(Integer.class), connection));
    }

    private void mockInsert(TicketType ticketType) throws SQLException {
        when(connection.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        doAnswer(invocation -> {
            ticketTypes.add(ticketType);
            return true;
        }).when(ps).execute();
    }

    private void mockTicketType(TicketType ticketType) throws SQLException {
        when(rs.getInt(1)).thenReturn(ticketType.getId());
        when(rs.getString(2)).thenReturn(ticketType.getName());
        when(rs.getInt(3)).thenReturn(ticketType.getPrice().intValue());
    }

    public void buildMock(List<TicketType> ticketTypeList) throws SQLException {
        index = -1;
        doAnswer(invocation -> {
            index++;
            if (index >= ticketTypeList.size())
                return false;
            mockTicketType(ticketTypeList.get(index));
            return true;
        }).when(rs).next();
    }

    private void setSessionList() {
        ticketTypes = new ArrayList<>();
        ticketTypes.add(new TicketType(1, "simple", new BigDecimal(120)));
        ticketTypes.add(new TicketType(2, "vip", new BigDecimal(120)));
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
