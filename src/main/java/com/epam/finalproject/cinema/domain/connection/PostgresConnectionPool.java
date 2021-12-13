package com.epam.finalproject.cinema.domain.connection;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * PostgresConnection pooling refers to the method of
 * creating a pool of connections to PostgresSQL database
 * and caching those connections so that it can be reused again.
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
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


    /**
     * This is the main method which is used to get connection with database.
     * It requires context.xml file with properties of connection.
     * @return Connection object.
     * @exception SQLException – if a database access error occurs.
     * @exception NamingException – if a naming exception is encountered
     * @see SQLException
     * @see NamingException
     */
    public synchronized Connection getConnection() throws SQLException, NamingException {
        Context context;
        Connection connection;
        context = new InitialContext();
        Context envContext = (Context) context.lookup("java:/comp/env");
        DataSource ds = (DataSource) envContext.lookup("jdbc/cinema");
        connection = ds.getConnection();
        return connection;
    }
}