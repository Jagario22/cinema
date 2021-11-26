package com.epam.finalproject.cinema.domain.entity;

import java.io.Serializable;
import java.util.Objects;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 5492383796645561523L;
    private Integer id;
    private Short number;
    private Integer ticketTypeId;
    private Integer sessionId;
    private Integer userId;

    public Ticket(Integer id, Short number, Integer ticketTypeId, Integer sessionId, Integer userId) {
        this.id = id;
        this.number = number;
        this.ticketTypeId = ticketTypeId;
        this.sessionId = sessionId;
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public Short getNumber() {
        return number;
    }

    public void setNumber(Short number) {
        this.number = number;
    }

    public Integer getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(Integer ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ticket ticket = (Ticket) o;
        return number.equals(ticket.number) && ticketTypeId.equals(ticket.ticketTypeId) && sessionId.equals(ticket.sessionId) && Objects.equals(userId, ticket.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, sessionId);
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", number=" + number +
                ", ticketTypeId=" + ticketTypeId +
                ", sessionId=" + sessionId +
                ", userId=" + userId +
                '}';
    }
}
