package com.epam.finalproject.cinema.domain.session;

public class SessionQuery {
    public final static String SELECT_FROM_SESSIONS_WHERE_DATETIME = "select * from sessions where date_time = ?";
    public final static String SELECT_SESSION_WITH_MAX_DATETIME_BEFORE = "select * " +
            "from sessions " +
            "where date_time = (select max(date_time) from sessions where date_time < ?)";
}
