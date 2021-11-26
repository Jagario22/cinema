package com.epam.finalproject.cinema.domain.dao;

import com.epam.finalproject.cinema.domain.connection.ConnectionPool;
import com.epam.finalproject.cinema.domain.connection.PostgresConnectionPool;
import com.epam.finalproject.cinema.domain.constants.PostgresQuery;
import com.epam.finalproject.cinema.domain.entity.User;
import com.epam.finalproject.cinema.util.CloseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private final ConnectionPool connectionPool;
    private final static Logger log = LogManager.getLogger(UserDao.class);

    private UserDao() {
        connectionPool = PostgresConnectionPool.getInstance();
        System.out.println();
    }

    private static UserDao instance = null;

    public static synchronized UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
        }
        return instance;
    }

    public int insertUser(User user, Connection connection) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement ps = null;
        int generatedKey;

        try {
            ps = connection.prepareStatement(PostgresQuery.INSERT_USER,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getLogin());
            ps.setObject(4, user.getRole().toString().toLowerCase());
            ps.execute();
            connection.commit();

            resultSet = ps.getGeneratedKeys();
            if (resultSet.next()) {
                generatedKey = resultSet.getInt(1);
            } else {
                String msg = "Inserting new user: " + user + " failed";
                log.error(msg);
                throw new SQLException(msg);
            }
        } finally {
            CloseUtil.close(ps, resultSet);
        }

        return generatedKey;
    }

    public List<User> findUsersWithEqualLoginOrEmail(String login, String email) throws SQLException, NamingException {
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement ps = null;
        List<User> users = new ArrayList<>();
        try {
            connection = connectionPool.getConnection();
            ps = connection.prepareStatement(PostgresQuery.SELECT_CASE_EQUAL_LOGIN_OR_EMAIL);
            ps.setString(1, login);
            ps.setString(2, email);
            ps.setString(3, login);
            ps.setString(4, email);
            resultSet = ps.executeQuery();

            if (resultSet.next()) {
                int caseEqualLogin = resultSet.getInt(1);
                int caseEqualEmail = resultSet.getInt(2);
                int caseLoginAndEmailOfOneUser = resultSet.getInt(3);
                if (caseLoginAndEmailOfOneUser == 1) {
                    User user = new User();
                    user.setEmail(email);
                    user.setLogin(login);
                    users.add(user);
                } else {
                    if (caseEqualEmail == 1) {
                        User user = new User();
                        user.setEmail(email);
                        users.add(user);
                    }
                    if (caseEqualLogin == 1) {
                        User user = new User();
                        user.setLogin(login);
                        users.add(user);
                    }
                }
                connection.commit();
            } else {
                String exMsg = "Getting user failed. Don't have result";
                log.error("Getting user with login:" + login + "failed" + "\n" + exMsg);
                throw new SQLException(exMsg);
            }

        } finally {
            CloseUtil.close(connection, ps, resultSet);
        }
        return users;
    }


    public User findUserByLoginAndPassword(String login, String password, Connection connection) throws SQLException, NamingException {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        User user = null;
        try {
            preparedStatement = connection.prepareStatement(PostgresQuery.SELECT_USER_BY_LOGIN_AND_PASSWORD);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                String email = resultSet.getString(2);
                User.ROLE role = User.ROLE.valueOf(resultSet.getString(5).toUpperCase());
                user = new User(id, email, login, role);
            }
            connection.commit();
        } finally {
            CloseUtil.close(preparedStatement, resultSet);
        }
        return user;
    }

}
