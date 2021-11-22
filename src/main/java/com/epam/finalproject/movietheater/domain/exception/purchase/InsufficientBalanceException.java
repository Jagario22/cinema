package com.epam.finalproject.movietheater.domain.exception.purchase;

public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}