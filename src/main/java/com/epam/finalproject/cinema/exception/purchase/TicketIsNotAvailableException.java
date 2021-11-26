package com.epam.finalproject.cinema.exception.purchase;

public class TicketIsNotAvailableException extends TicketPurchaseException {
    public TicketIsNotAvailableException(String message) {
        super(message);
    }
}