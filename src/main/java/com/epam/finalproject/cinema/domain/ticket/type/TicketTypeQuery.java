package com.epam.finalproject.cinema.domain.ticket.type;
/**
 * Constants with PostgresSQL queries of ticket type entity.
 * Works with PostgresSQL dialect
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class TicketTypeQuery {
    public final static String SELECT_TICKET_TYPE_BY_ID = "select * from ticket_types where id = ?";
    public final static String INSERT_INTO_TICKET_TYPES_VALUES = "insert into ticket_types (name, price) VALUES (?, ?)";
}
