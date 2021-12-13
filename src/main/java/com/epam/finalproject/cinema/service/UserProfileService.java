package com.epam.finalproject.cinema.service;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.user.User;
import com.epam.finalproject.cinema.domain.wallet.Wallet;
import com.epam.finalproject.cinema.domain.user.UserDao;
import com.epam.finalproject.cinema.domain.wallet.WalletDao;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.web.model.user.UserProfileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
/**
 * Provides interactions between a client App and user database entity, that allows interaction with wallet entity
 * Provides operations with User DTO object.
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class UserProfileService {
    private static UserProfileService instance = null;
    private final UserDao userDao = UserDao.getInstance();
    private final WalletDao walletDao = WalletDao.getInstance();
    private final ConnectionPool connectionPool = PostgresConnectionPool.getInstance();
    private final static Logger log = LogManager.getLogger(FilmService.class);

    public UserProfileService() {
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
            userId = userDao.insert(user, connection);
            Wallet wallet = new Wallet(userId, BigDecimal.ZERO);
            walletDao.insert(wallet, connection);
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Creating user failed";
            connectionRollback(connection, msg);
            throw new DBException(msg, e);
        } finally {
            closeConnection(connection, "Creating user failed");
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
            userInfo = convertToUserProfileInfo(new User(user.getId(), user.getEmail(),
                    user.getLogin(), user.getRole()), wallet);
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Getting user failed";
            log.error(msg + "\n" + " login: " + login + " password: " + password);
            connectionRollback(connection, msg);
            throw new DBException(msg, e);
        } finally {
            String errorMsg = "Getting user failed. " + "\n" + " login: " + login + " password: " + password;
            closeConnection(connection, errorMsg);
        }
        return userInfo;
    }

    public BigDecimal getWalletBalanceByUserId(int userId) throws DBException {
        Connection connection = null;
        BigDecimal balance;
        try {
            connection = connectionPool.getConnection();
            balance = walletDao.findBalanceByUserId(userId, connection);
        } catch (SQLException | NamingException e) {
            String msg = "Getting wallet user failed";
            log.error(msg + "\nuserId:" + userId);
            connectionRollback(connection, msg);
            throw new DBException(msg, e);
        } finally {
            closeConnection(connection, "Getting user balance failed");
        }

        return balance;
    }


    public List<User> getUsersWithEqualLoginOrEmail(String login, String email) throws DBException {
        Connection connection = null;
        List<User> users;
        try {
            connection = connectionPool.getConnection();
            users = userDao.findUsersWithEqualLoginOrEmail(login, email, connection);
            connection.commit();
        } catch (SQLException | NamingException e) {
            String msg = "Getting user failed";
            connectionRollback(connection, msg);
            log.error(msg + " with login " + login + " and email " + email);
            throw new DBException(msg, e);
        } finally {
            String msg = "Getting user failed";
            closeConnection(connection, msg);

        }
        return users;
    }

    public void topUpBalanceByUserId(Integer userId, BigDecimal amount) throws DBException {
        Connection connection = null;
        try {
            connection = connectionPool.getConnection();
            topUpBalanceByUserId(userId, amount, connection);
            connection.commit();
        } catch (SQLException | NamingException e) {
            String errorMsg = "Updating user: " + userId + " balance failed";
            connectionRollback(connection, errorMsg);
            throw new DBException("Updating user balance failed", e);
        } finally {
            closeConnection(connection, "Updating user balance failed");
        }
    }

    public void topUpBalanceByUserId(Integer userId, BigDecimal amount, Connection connection) throws SQLException {
        try {
            BigDecimal balance = walletDao.findBalanceByUserId(userId, connection);
            BigDecimal newBalance = balance.add(amount);
            walletDao.updateOnBalanceByUserId(userId, newBalance, connection);

        } catch (SQLException e) {
            log.error("Updating user: " + userId + " balance failed\n" +
                    e.getMessage());
            throw e;
        }
    }

    private UserProfileInfo convertToUserProfileInfo(User user, Wallet wallet) {
        return new UserProfileInfo(user.getId(), user.getLogin(), user.getEmail(), wallet, user.getRole());
    }

    private void connectionRollback(Connection connection, String errorMsg) throws DBException {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DBException(errorMsg, e);
        }
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
}