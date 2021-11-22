package com.epam.finalproject.movietheater.domain.exception.purchase;

public class InactiveFilmSessionException extends Exception {
    public InactiveFilmSessionException(String message) {
        super(message);
    }
}