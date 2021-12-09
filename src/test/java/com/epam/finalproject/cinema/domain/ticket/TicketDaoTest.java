package com.epam.finalproject.cinema.domain.ticket;

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
public class TicketDaoTest {
    private final static Logger log = Logger.getLogger(TicketDaoTest.class.getName());
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

    private TicketDao ticketDao;
    private List<Ticket> tickets;

    @Before
    public void setUp() throws SQLException, NamingException {
        System.setProperty("logFile", "src\\test\\test_log.log");
        when(connectionPool.getConnection()).thenReturn(connection);

        try (MockedStatic<PostgresConnectionPool> theMock = Mockito.mockStatic(PostgresConnectionPool.class)) {
            theMock.when(PostgresConnectionPool::getInstance).thenReturn(connectionPool);
            ticketDao = TicketDao.getInstance();
        }
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(any(String.class))).thenReturn(rs);
        when(connection.prepareStatement(any())).
                thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        setSessionList();
        connection = connectionPool.getConnection();
        ticketDao = TicketDao.getInstance();
    }


    @Test
    public void shouldReturnTicketWhenGettingByIdAndNullUserId() throws SQLException {
        Ticket ticket = tickets.get(0);
        when(rs.next()).thenReturn(true);
        mockTicket(ticket);
        assertEquals(ticket, ticketDao.findTicketByIdWhereUserIdIsNull(any(Integer.class), connection));
    }

    @Test
    public void shouldReturnTicketsWhenGettingBySessionId() throws SQLException {
        buildMock(tickets);
        assertEquals(tickets, ticketDao.findAllTicketsOfSession(any(Integer.class), connection));
    }

    @Test
    public void shouldReturnTicketsWhenGettingByUserId() throws SQLException {
        buildMock(tickets);
        assertEquals(tickets, ticketDao.findTicketsByUserId(any(Integer.class), connection));
    }


    @Test
    public void shouldReturnTicketsWhenGettingBySessionIdAndNullUserId() throws SQLException {
        buildMock(tickets);
        assertEquals(tickets, ticketDao.findTicketsBySessionIdWhereUserIsNull(any(Integer.class), connection));
    }

    @Test
    public void shouldInsertTicketAndReturnGeneratedKey() throws SQLException {
        int size = tickets.size();
        Ticket newTicket = new Ticket(size + 1, (short) 4, 1, 1, 1);
        mockInsert(newTicket);
        int generatedKey = size + 1;
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt(1)).thenReturn(generatedKey);
        assertEquals(generatedKey, ticketDao.insert(newTicket, connection));
        assertEquals(size + 1, tickets.size());
    }

    @Test
    public void shouldUpdateTicket() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);
        ticketDao.updateTicketOnUserIdById(1, 1, connection);
    }

    @Test(expected = SQLException.class)
    public void shouldThrowExceptionIfTicketNotUpdated() throws SQLException {
        when(ps.executeUpdate()).thenReturn(0);
        ticketDao.updateTicketOnUserIdById(1, 1, connection);
    }

    private void setSessionList() {
        tickets = new ArrayList<>();
        tickets.add(new Ticket(1, (short) 1, 1, 1, 1));
        tickets.add(new Ticket(2, (short) 2, 1, 1, 0));
        tickets.add(new Ticket(3, (short) 3, 1, 1, 0));
    }

    private void mockInsert(Ticket ticket) throws SQLException {
        when(connection.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        doAnswer(invocation -> {
            tickets.add(ticket);
            return true;
        }).when(ps).execute();
    }

    private void mockTicket(Ticket ticket) throws SQLException {
        when(rs.getInt(1)).thenReturn(ticket.getId());
        when(rs.getShort(2)).thenReturn(ticket.getNumber());
        when(rs.getInt(3)).thenReturn(ticket.getTicketTypeId());
        when(rs.getInt(4)).thenReturn(ticket.getSessionId());
        when(rs.getInt(5)).thenReturn(ticket.getUserId());
    }

    public void buildMock(List<Ticket> ticketList) throws SQLException {
        index = -1;
        doAnswer(invocation -> {
            index++;
            if (index >= ticketList.size())
                return false;
            mockTicket(ticketList.get(index));
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
