package com.epam.finalproject.cinema.util;

import java.sql.*;

public class CloseUtil {
    public static void close(Connection connection, Statement st, ResultSet rs) throws SQLException {
        if (connection != null) {
            connection.close();
        }

        if (rs != null) {
            rs.close();
        }

        if (st != null) {
            st.close();
        }
    }

    public static  void close(Connection connection, PreparedStatement st, ResultSet rs) throws SQLException {
        if (connection != null) {
            connection.close();
        }

        if (rs != null) {
            rs.close();
        }

        if (st != null) {
            st.close();
        }
    }

    public static void close(PreparedStatement ps, ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }

        if (ps != null) {
            ps.close();
        }
    }

    public static void close(Statement st, ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }

        if (st != null) {
            st.close();
        }
    }
}
