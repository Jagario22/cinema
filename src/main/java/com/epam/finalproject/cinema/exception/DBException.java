package com.epam.finalproject.cinema.exception;


import java.sql.SQLException;

/**
 * Exception that provides information on a database access error

 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class DBException extends Exception {
    public DBException(String message, Throwable cause) {
        super(message, cause);
    }




}
