package com.epam.finalproject.movietheater.domain.dao;

import com.epam.finalproject.movietheater.domain.exception.DBException;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CrudDAO<T> {
    T get(int id) throws SQLException, NamingException, IOException, DBException;
    int save(T t);
    void update(T t);
    void delete(T t);
}
