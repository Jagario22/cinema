package com.epam.finalproject.cinema.domain.dao;

import com.epam.finalproject.cinema.exception.DBException;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.SQLException;

public interface CrudDAO<T> {
    T get(int id) throws SQLException, NamingException, IOException, DBException;
    int save(T t);
    void update(T t);
    void delete(T t);
}
