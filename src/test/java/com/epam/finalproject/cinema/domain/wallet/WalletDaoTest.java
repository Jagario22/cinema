package com.epam.finalproject.cinema.domain.wallet;

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
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class WalletDaoTest {
    private final static Logger log = Logger.getLogger(WalletDao.class.getName());
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

    private WalletDao walletDao;
    private List<Wallet> wallets;

    @Before
    public void setUp() throws SQLException, NamingException {
        System.setProperty("logFile", "src\\test\\test_log.log");
        when(connectionPool.getConnection()).thenReturn(connection);

        try (MockedStatic<PostgresConnectionPool> theMock = Mockito.mockStatic(PostgresConnectionPool.class)) {
            theMock.when(PostgresConnectionPool::getInstance).thenReturn(connectionPool);
            walletDao = WalletDao.getInstance();
        }
        when(connection.createStatement()).thenReturn(stmt);
        when(stmt.executeQuery(any(String.class))).thenReturn(rs);
        when(connection.prepareStatement(any())).
                thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        setSessionList();
        connection = connectionPool.getConnection();
        walletDao = WalletDao.getInstance();
    }


    private void setSessionList() {
        wallets = new ArrayList<>();
        wallets.add(new Wallet(1, 1, new BigDecimal("100.0")));
        wallets.add(new Wallet(2, 2, new BigDecimal("200.0")));
        wallets.add(new Wallet(3, 3, new BigDecimal("300.0")));
    }

    @Test
    public void shouldInsertWalletThenReturnGeneratedKey() throws SQLException {
        int size = wallets.size();
        Wallet newWallet = new Wallet(size + 1, 1, new BigDecimal(0));
        mockInsert(newWallet);
        int generatedKey = size + 1;
        when(rs.next()).thenReturn(true).thenReturn(false);
        when(rs.getInt(1)).thenReturn(generatedKey);
        assertEquals(generatedKey, walletDao.insert(newWallet, connection));
        assertEquals(size + 1, wallets.size());
    }

    @Test
    public void shouldUpdateOnBalanceByUserId() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);
        walletDao.updateOnBalanceByUserId(any(Integer.class), new BigDecimal(0), connection);
    }

    @Test(expected = SQLException.class)
    public void shouldThrowExceptionIfWalletNotUpdatedOnBalanceByUserId() throws SQLException {
        when(ps.executeUpdate()).thenReturn(0);
        walletDao.updateOnBalanceByUserId(1, new BigDecimal(0), connection);
    }


    @Test
    public void shouldReturnBalanceBalanceWhenGettingByUserId() throws SQLException {
        BigDecimal expectedBalance = new BigDecimal("100.0");
        when(rs.getDouble(1)).thenReturn(expectedBalance.doubleValue());
        when(rs.next()).thenReturn(true);
        assertEquals(expectedBalance, walletDao.findBalanceByUserId(any(Integer.class), connection));
    }

    @Test
    public void shouldReturnWalletWhenGettingByUserId() throws SQLException {
        Wallet expectedWallet = wallets.get(0);
        when(rs.next()).thenReturn(true);
        mockWallet(expectedWallet);
        assertEquals(expectedWallet, walletDao.findWalletByUserId(any(Integer.class), connection));
    }

    private void mockInsert(Wallet wallet) throws SQLException {
        when(connection.prepareStatement(any(String.class), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        doAnswer(invocation -> {
            wallets.add(wallet);
            return true;
        }).when(ps).execute();
    }

    private void mockWallet(Wallet wallet) throws SQLException {
        when(rs.getInt(1)).thenReturn(wallet.getId());
        when(rs.getInt(2)).thenReturn(wallet.getUserId());
        when(rs.getDouble(3)).thenReturn(wallet.getBalance().doubleValue());
    }

    public void buildMock(List<Wallet> walletList) throws SQLException {
        index = -1;
        doAnswer(invocation -> {
            index++;
            if (index >= walletList.size())
                return false;
            mockWallet(walletList.get(index));
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
