package com.epam.finalproject.cinema.domain.connection;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionPool {
    Connection getConnection() throws SQLException, NamingException;
}
