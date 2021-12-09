package com.epam.finalproject.cinema.domain.session;

public class SessionQuery {
    public final static String SELECT_FROM_SESSIONS_WHERE_DATETIME = "select * from sessions where date_time = ?";
    public final static String SELECT_SESSION_WITH_MAX_DATETIME_BEFORE = "select * " +
            "from sessions " +
            "where date_time = (select max(date_time) from sessions where date_time < ?)";
    public final static String SELECT_CURRENT_SESSIONS_OF_FILM = "select distinct s.* from sessions as s inner join films " +
            "f on f.id = s.film_id " +
            " where (date_time > now() and film_id=?) order by (s.date_time);";
    public final static String SELECT_SESSION_BY_ID = "select * from sessions where id = ?";
    public final static String SELECT_CURRENT_SESSION_BY_ID = "select * from sessions where (date_time > now() and id = ?)";
    public final static String INSERT_INTO_SESSIONS = "insert into sessions (date_time, lang, film_id) values (?, ?::\"lang\", " +
            "?)";
    public final static String DELETE_FROM_SESSIONS_WHERE_ID_IS = "delete from sessions where id = ?";
    public final static String SESSIONS_DATE_TIME_FIELD = "s.date_time";
    public final static String SELECT_SESSIONS_AFTER_DATE = "select * from sessions where date_time > ?";
}
