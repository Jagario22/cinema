package com.epam.finalproject.cinema.domain.user;

import com.epam.finalproject.cinema.util.CloseUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.epam.finalproject.cinema.domain.user.UserQuery.*;

public class UserDao {
    private final static Logger log = LogManager.getLogger(UserDao.class);

    private UserDao() {
        System.out.println();
    }

    private static UserDao instance = null;

    public static synchronized UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
        }
        return instance;
    }

    public int insert(User user, Connection connection) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement ps = null;
        int generatedKey;

        try {
            ps = connection.prepareStatement(INSERT_USER,
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
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            try {
                CloseUtil.close(ps, resultSet);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }

        return generatedKey;
    }

    public List<User> findUsersWithEqualLoginOrEmail(String login, String email, Connection connection) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement ps = null;
        List<User> users = new ArrayList<>();
        try {
            ps = connection.prepareStatement(SELECT_CASE_EQUAL_LOGIN_OR_EMAIL);
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
            } else {
                throw new SQLException("Getting user failed. Don't have result");
            }

        } catch (SQLException e) {
            log.error(e);
            throw e;
        } finally {
            try {
                CloseUtil.close(ps, resultSet);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return users;
    }


    public User findUserByLoginAndPassword(String login, String password, Connection connection) throws SQLException {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        User user = null;
        try {
            preparedStatement = connection.prepareStatement(SELECT_USER_BY_LOGIN_AND_PASSWORD);
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = readUser(resultSet);
            }

        } finally {
            try {
                CloseUtil.close(preparedStatement, resultSet);
            } catch (SQLException e) {
                log.error(e.getMessage());
            }
        }
        return user;
    }

    private User readUser(ResultSet rs) throws SQLException {
        User user;
        int id = rs.getInt(1);
        String email = rs.getString(2);
        String password = rs.getString(3);
        String login = rs.getString(4);
        User.ROLE role = User.ROLE.valueOf(rs.getString(5).toUpperCase());
        user = new User(id, email, password, login, role);

        return user;
    }

}
