package com.epam.finalproject.cinema.domain.dao;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.constants.PostgresQuery;
import com.epam.finalproject.cinema.domain.entity.Wallet;
import com.epam.finalproject.cinema.util.CloseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.*;

public class WalletDao {
    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(WalletDao.class);

    private WalletDao() {
        connectionPool = PostgresConnectionPool.getInstance();
    }

    private static WalletDao instance = null;

    public static synchronized WalletDao getInstance() {
        if (instance == null) {
            instance = new WalletDao();
        }
        return instance;
    }


    public void updateOnBalanceByUserId(int userId, BigDecimal balance, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(PostgresQuery.UPDATE_WALLET_ON_BALANCE_BY_USER_ID);
            ps.setDouble(1, balance.doubleValue());
            ps.setInt(2, userId);

            int result = ps.executeUpdate();
            if (result != 1) {
                throw new SQLException("Updating wallet balance by user id: " +
                        userId + " was failed");
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    public BigDecimal findBalanceByUserId(int userId, Connection connection) throws SQLException {
        BigDecimal balance = null;
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(PostgresQuery.SELECT_WALLET_BALANCE_BY_USER_ID);
            ps.setInt(1, userId);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                balance = BigDecimal.valueOf(resultSet.getDouble(1));
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            CloseUtil.close(ps, resultSet);
        }
        return balance;

    }

    public void insertWallet(Wallet wallet, Connection connection) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement ps = null;
        int generatedKey;

        try {
            ps = connection.prepareStatement(PostgresQuery.INSERT_WALLET,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, wallet.getUserId());
            ps.setDouble(2, wallet.getBalance().doubleValue());
            ps.execute();
            connection.commit();

            resultSet = ps.getGeneratedKeys();
            if (resultSet.next()) {
                generatedKey = resultSet.getInt(1);

                if (generatedKey <= 0) {
                    String msg = "Inserting wallet: " + wallet + " failed";
                    log.error(msg);
                    throw new SQLException(msg);
                }
            } else {
                String msg = "Inserting wallet: " + wallet + " failed";
                log.error(msg);
                throw new SQLException(msg);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            CloseUtil.close(ps, resultSet);
        }

    }

    public Wallet findWalletByUserId(Integer userId, Connection connection) throws SQLException {
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        Wallet wallet = null;
        try {
            ps = connection.prepareStatement(PostgresQuery.SELECT_WALLET_BY_USER_ID);
            ps.setInt(1, userId);
            resultSet = ps.executeQuery();
            if (resultSet.next()) {
                wallet = readWallet(resultSet);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            CloseUtil.close(ps, resultSet);
        }
        return wallet;
    }

    private Wallet readWallet(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        int userId = rs.getInt(2);
        BigDecimal balance = BigDecimal.valueOf(rs.getDouble(3));
        return new Wallet(id, userId, balance);
    }
}
