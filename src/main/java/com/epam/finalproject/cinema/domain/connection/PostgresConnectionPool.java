package com.epam.finalproject.cinema.domain.connection;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class PostgresConnectionPool implements ConnectionPool {
    private PostgresConnectionPool() {

    }
    private static PostgresConnectionPool instance = null;

    public static synchronized PostgresConnectionPool getInstance() {
        if (instance == null) {
            instance = new PostgresConnectionPool();
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException, NamingException {
        Context context;
        Connection connection = null;
        context = new InitialContext();
        Context envContext = (Context) context.lookup("java:/comp/env");
        DataSource ds = (DataSource) envContext.lookup("jdbc/cinema");
        connection = ds.getConnection();
        return connection;
    }
}