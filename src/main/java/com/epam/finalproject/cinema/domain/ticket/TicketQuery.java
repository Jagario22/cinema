package com.epam.finalproject.cinema.domain.ticket;
/**
 * Constants with PostgresSQL queries of ticket entity.
 * Works with PostgresSQL dialect
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class TicketQuery {
    public static final String SELECT_TICKETS_BY_SESSION_ID_WHERE_USER_IS_NOT_NULL = "select count(*) from " +
            " (select * from tickets where user_id is not null and session_id=?)";
    public final static String INSERT_INTO_TICKETS_VALUES = "insert into tickets (number, ticket_type_id, session_id) " +
            "values (?, ?, ?)";
    public final static String SELECT_TICKETS_BY_SESSION_ID_WHERE_USER_ID_IS_NULL = "select t.* from sessions as s inner join tickets " +
            "t on s.id = t.session_id where (s.id = ? and t.user_id is null)";
    public final static String SELECT_TICKETS_OF_SESSION = "select *  from tickets where session_id = ?";
    public final static String SELECT_TICKET_BY_ID_WHERE_USER_ID_IS_NULL = "select * from tickets where id=? and user_id is null";
    public final static String UPDATE_TICKET_ON_USER_ID_BY_ID = "update tickets set user_id=? where id=? ";
    public final static String SELECT_TICKETS_BY_USER_ID = "select t.* from tickets as t inner join sessions s on s.id = t.session_id " +
            "where s.date_time > now() and t.user_id = ?";
}
