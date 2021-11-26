package com.epam.finalproject.cinema.exception.purchase;

public class InactiveFilmSessionException extends Exception {
    public InactiveFilmSessionException(String message) {
        super(message);
    }
}