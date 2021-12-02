package com.epam.finalproject.cinema.web.model.ticket;

import com.epam.finalproject.cinema.domain.entity.TicketType;
import com.epam.finalproject.cinema.web.constants.CinemaConstants;
import com.epam.finalproject.cinema.web.model.film.session.SessionInfo;

import java.io.Serializable;

public class TicketInfo implements Serializable {
    private Integer id;
    private Short placeNumber;
    private TicketType ticketType;
    private SessionInfo sessionInfo;
    private Integer row;
    private int userId;

    public TicketInfo(Integer id, Short placeNumber, TicketType ticketType, int userId) {
        this.id = id;
        this.placeNumber = placeNumber;
        this.ticketType = ticketType;
        this.row = calculateRow(placeNumber);
    }

    private Integer calculateRow(Short placeNumber) {
        if (placeNumber >= 15) {
            return placeNumber / CinemaConstants.COUNT_OF_ROW_SEAT;
        }

        return 1;
    }

    public TicketInfo(Integer id, Short placeNumber, TicketType ticketType, SessionInfo sessionInfo, int userId) {
        this.id = id;
        this.placeNumber = placeNumber;
        this.ticketType = ticketType;
        this.sessionInfo = sessionInfo;
        this.row = calculateRow(placeNumber);
        this.userId = userId;
    }

    public void setRow(Integer row) {
        this.row = row;
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

    public SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    public void setSessionInfo(SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getRow() {
        return row;
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
