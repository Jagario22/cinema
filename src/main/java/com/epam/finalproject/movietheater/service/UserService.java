package com.epam.finalproject.movietheater.service;

import com.epam.finalproject.movietheater.domain.connection.ConnectionPool;
import com.epam.finalproject.movietheater.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.movietheater.domain.dao.*;
import com.epam.finalproject.movietheater.domain.entity.User;
import com.epam.finalproject.movietheater.domain.entity.Wallet;
import com.epam.finalproject.movietheater.domain.exception.DBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {
    private static UserService instance = null;
    private final UserDao userDao;
    private final WalletDao walletDao;
    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(FilmService.class);

    public UserService() {
        userDao = UserDao.getInstance();
        walletDao = WalletDao.getInstance();
        connectionPool = PostgresConnectionPool.getInstance();
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
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

    public User findUserByLoginAndPassword(String login, String password) throws DBException {
        User user = null;
        try {
            user = userDao.findUserByLoginAndPassword(login, password);
        } catch (SQLException | NamingException e) {
            log.error("Getting user with login:" + login + " and password: " + password + " failed"
                    + "\n" + e.getMessage());
            throw new DBException("Checking user failed", e);
        }
        return user;
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
}