package com.epam.finalproject.cinema.domain.connection;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * ConnectionPool interface provides method for getting connection from pool
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public interface ConnectionPool {
    Connection getConnection() throws SQLException, NamingException;
}
