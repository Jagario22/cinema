package com.epam.finalproject.cinema.domain.user;

public class UserQuery {
    public final static String INSERT_USER = "insert into users(email, password, login, role) values (?, ?, ?, ?::\"role_type\")";
    public static final String SELECT_CASE_EQUAL_LOGIN_OR_EMAIL = "SELECT  COUNT(CASE WHEN login = ?  and role='user' THEN 1 END)  login_count," +
            " COUNT(CASE WHEN email = ? and role='user' THEN 1 END) email_count," +
            " COUNT(CASE WHEN login = ? and email = ? and role = 'user' THEN 1 END) as one_user" +
            " FROM users";
    public final static String SELECT_USER_BY_LOGIN_AND_PASSWORD = "select * from users where login = ? AND password = ?";
}
