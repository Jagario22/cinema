package com.epam.finalproject.movietheater.domain.exception.purchase;

public class TicketIsNotAvailableException extends TicketPurchaseException {
    public TicketIsNotAvailableException(String message) {
        super(message);
    }
}