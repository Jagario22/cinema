package com.epam.finalproject.movietheater.domain.constants;

public class PostgresQuery {
    public static final String INSERT_USER = "insert into users(email, password, login, role) values (?, ?, ?, ?::\"role_type\")";
    public static final String SELECT_CASE_EQUAL_LOGIN_OR_EMAIL = "SELECT  COUNT(CASE WHEN login = ?  and role='user' THEN 1 END)  login_count," +
            " COUNT(CASE WHEN email = ? and role='user' THEN 1 END) email_count," +
            " COUNT(CASE WHEN login = ? and email = ? and role = 'user' THEN 1 END) as one_user" +
            " FROM users";
    public static final String SELECT_FILM_BY_ID = "select * from films where id = ?";
    public static final String SELECT_TICKET_BY_ID_WHERE_USER_ID_IS_NULL = "select * from tickets where id=? and user_id is null";
    public static final String INSERT_WALLET = "insert into wallets(user_id, balance) values(?, ?)";
    public static String SELECT_TITLE_FROM_FILMS = "select (id, title) from films";
    public static String SELECT_USER_BY_LOGIN_AND_PASSWORD = "select * from users where login = ? AND password = ?";
    public static String SELECT_ALL_CURRENT_FILMS = "select f.* from films as f  INNER JOIN sessions ON f.id = sessions.id " +
            "where (f.last_showing_date > now()::date - 365 and sessions.film_id is not null);";
    public static String SELECT_CURRENT_SESSIONS_OF_FILM = "select distinct s.* from sessions as s inner join films f on f.id = s.film_id " +
            "inner join tickets t on s.id = t.session_id where (date_time > now() and film_id=?);";
    public static String SELECT_ALL_GENRES_OF_FILM = "select * from genres inner join genres_films gf on genres.id = gf.genre_id \n" +
            "    inner join films f on f.id = gf.film_id where f.id = ?";
    public static String SELECT_TICKETS_BY_SESSION_ID_WHERE_USER_ID_IS_NULL = "select t.* from sessions as s inner join tickets t on s.id = t.session_id " +
            "where (s.id = ? and t.user_id is null)";
    public static String SELECT_TICKET_TYPE_BY_ID = "select * from ticket_types where id = ?";
    public static String SELECT_SESSION_BY_ID = "select * from sessions where id = ?";
    public static String SELECT_CURRENT_SESSION_BY_ID = "select * from sessions where (date_time > now() and id = ?)";
    public static String SELECT_WALLET_BALANCE_BY_USER_ID = "select balance from wallets where user_id=?";
    public static String UPDATE_WALLET_ON_BALANCE_BY_USER_ID = "update wallets set balance=? where user_id=? ";
    public static String UPDATE_TICKET_ON_USER_ID_BY_ID = "update tickets set user_id=? where id=? ";
    public static String SELECT_TICKET_COUNT_OF_FILM_WHERE_USER_IS_NULL_GROUP_BY_SESSION_ID =
            "select session_id,count(*) from (select * from films inner join sessions s on films.id = s.film_id " +
                    "inner join tickets t on s.id = t.session_id " +
                    "where film_id = ? and t.user_id is null) as c group by session_id";

}

