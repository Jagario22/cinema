package com.epam.finalproject.movietheater.web.model;
import com.epam.finalproject.movietheater.domain.entity.TicketType;

public class TicketDTO {
    private Integer id;
    private Short placeNumber;
    private TicketType ticketType;

    public TicketDTO(Integer id, Short placeNumber, TicketType ticketType) {
        this.id = id;
        this.placeNumber = placeNumber;
        this.ticketType = ticketType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Short getPlaceNumber() {
        return placeNumber;
    }

    public void setPlaceNumber(Short placeNumber) {
        this.placeNumber = placeNumber;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    @Override
    public String toString() {
        return "TicketDTO{" +
                "id=" + id +
                ", placeNumber=" + placeNumber +
                ", ticketType=" + ticketType +
                '}';
    }
}
