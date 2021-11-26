package com.epam.finalproject.cinema.exception.purchase;

public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}