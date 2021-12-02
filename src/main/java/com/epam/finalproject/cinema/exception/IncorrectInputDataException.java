package com.epam.finalproject.cinema.exception;

public class IncorrectInputDataException extends RuntimeException {
    public IncorrectInputDataException(String message) {
        super(message);
    }
}