package com.epam.finalproject.cinema.service;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.dao.*;
import com.epam.finalproject.cinema.domain.entity.User;
import com.epam.finalproject.cinema.domain.entity.Wallet;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.web.constants.Params;
import com.epam.finalproject.cinema.web.model.user.UserProfileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserProfileService {
    private static UserProfileService instance = null;
    private final UserDao userDao;
    private final WalletDao walletDao;
    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(FilmService.class);

    public UserProfileService() {
        userDao = UserDao.getInstance();
        walletDao = WalletDao.getInstance();
        connectionPool = PostgresConnectionPool.getInstance();
    }

    public static synchronized UserProfileService getInstance() {
        if (instance == null) {
            instance = new UserProfileService();
        }
        return instance;
    }


    public int creatingUser(User user) throws DBException {
        Connection connection = null;
        int userId;
        try {
            connection = connectionPool.getConnection();
            userId = userDao.insertUser(user, connection);
            Wallet wallet = new Wallet(userId, BigDecimal.ZERO);
            walletDao.insertWallet(wallet, connection);
            connection.commit();
        } catch (SQLException | NamingException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                String msg = "Creating the user failed";
                log.debug(msg + "\n" + e.getMessage());
                throw new DBException(msg, e);
            }
            String msg = "Creating the user failed";
            log.debug(msg + "\n" + e.getMessage());
            throw new DBException(msg, e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("closing connection was failed");
                }
            }
        }
        return userId;
    }

    public UserProfileInfo findUserByLoginAndPassword(String login, String password) throws DBException {
        UserProfileInfo userInfo = null;
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            User user = userDao.findUserByLoginAndPassword(login, password, connection);
            if (user == null)
                return null;
            Wallet wallet = null;
            if (user.getRole().equals(User.ROLE.USER)) {
                wallet = walletDao.findWalletByUserId(user.getId(), connection);

            }
            userInfo = convertToUserProfileInfo(user, wallet);
            connection.commit();
        } catch (SQLException | NamingException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                log.error("Getting user with login:" + login + " and password: " + password + " failed"
                        + "\n" + e.getMessage());
                throw new DBException("Getting user failed.", e);
            }
            log.error("Getting user with login:" + login + " and password: " + password + " failed"
                    + "\n" + e.getMessage());
            throw new DBException("Getting user failed.", e);
        } finally {
            String errorMsg = "Getting user failed.";
            closeConnection(connection, errorMsg);
        }
        return userInfo;
    }

    public BigDecimal getWalletBalanceByUserId(int userId) throws DBException {
        Connection connection = null;
        BigDecimal balance = null;
        try {
            connection = connectionPool.getConnection();
            balance = walletDao.findBalanceByUserId(userId, connection);
        } catch (SQLException | NamingException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                log.error("Getting user balance failed"
                        + "\n" + e.getMessage());
                throw new DBException("Getting user balance failed", e);
            }
            log.error("Getting user balance failed"
                    + "\n" + e.getMessage());
            throw new DBException("Getting user balance failed", e);
        } finally {
            closeConnection(connection, "Getting user balance failed");
        }

        return balance;
    }


    private void closeConnection(Connection connection, String errorMsg) {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error(errorMsg + "\n" + e.getMessage());
        }
    }

    public List<User> findUsersWithEqualLoginOrEmail(String login, String email) throws DBException {
        try {
            return userDao.findUsersWithEqualLoginOrEmail(login, email);
        } catch (SQLException | NamingException e) {
            log.error("Getting user with login: " + login + " or email: " + email + "failed\n" +
                    e.getMessage());
            throw new DBException("Login and password validation failed", e);
        }
    }


    private UserProfileInfo convertToUserProfileInfo(User user, Wallet wallet) {
        return new UserProfileInfo(user.getId(), user.getLogin(), user.getEmail(), wallet, user.getRole());
    }


    public void topUpBalanceByUserId(Integer userId, BigDecimal amount) throws DBException {
        Connection connection = null;

        try {
            connection = connectionPool.getConnection();
            BigDecimal balance = walletDao.findBalanceByUserId(userId, connection);
            BigDecimal newBalance = balance.add(amount);
            walletDao.updateOnBalanceByUserId(userId, newBalance, connection);
            connection.commit();
        } catch (SQLException | NamingException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                log.error("Updating user: " + userId + " balance failed\n" +
                        e.getMessage());
                throw new DBException("Updating user balance failed", e);
            }
            log.error("Updating user: " + userId + " balance failed\n" +
                    e.getMessage());
            throw new DBException("Updating user balance failed", e);
        } finally {
            closeConnection(connection, "Updating user balance failed");
        }
    }
}