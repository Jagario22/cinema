package com.epam.finalproject.movietheater.domain.exception;

import java.sql.SQLException;

public class DBException extends Exception {
    public DBException(String message, Throwable cause) {
        super(message, cause);
    }
}
