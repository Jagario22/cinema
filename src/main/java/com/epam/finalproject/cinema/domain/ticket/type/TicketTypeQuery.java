package com.epam.finalproject.cinema.domain.ticket.type;

public class TicketTypeQuery {
    public final static String SELECT_TICKET_TYPE_BY_ID = "select * from ticket_types where id = ?";
    public final static String INSERT_INTO_TICKET_TYPES_VALUES = "insert into ticket_types (name, price) VALUES (?, ?)";
}
