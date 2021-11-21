package com.epam.finalproject.movietheater.domain.constants;

public class PostgresQuery {
    public static final String INSERT_USER = "insert into users(email, password, login, role) values (?, ?, ?, ?::\"role_type\")";
    public static final String SELECT_CASE_EQUAL_LOGIN_OR_EMAIL = "SELECT  COUNT(CASE WHEN login = ?  and role='user' THEN 1 END)  login_count," +
            " COUNT(CASE WHEN email = ? and role='user' THEN 1 END) email_count," +
            " COUNT(CASE WHEN login = ? and email = ? and role = 'user' THEN 1 END) as one_user" +
            " FROM users";
    public static final String SELECT_FILM_WHERE_ID_IS = "select * from films where id = ?";
    public static String SELECT_TITLE_FROM_FILMS = "select (id, title) from films";
    public static String SELECT_USER_BY_LOGIN_AND_PASSWORD = "select * from users where login = ? AND password = ?";
    public static String SELECT_ALL_CURRENT_FILMS = "select f.* from films as f  INNER JOIN sessions ON f.id = sessions.id " +
            "where (f.last_showing_date > now()::date - 365 and sessions.film_id is not null);";
    public static String SELECT_ALL_CURRENT_SESSION_OF_FILM = "select s.* from sessions as s inner join films f on f.id = s.film_id\n" +
            "            where (date_time > now() and film_id=?);";
    public static String SELECT_ALL_GENRES_OF_FILM = "select * from genres inner join genres_films gf on genres.id = gf.genre_id \n" +
            "    inner join films f on f.id = gf.film_id where f.id = ?";
}

