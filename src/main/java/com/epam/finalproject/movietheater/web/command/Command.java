package com.epam.finalproject.movietheater.web.command;

import com.epam.finalproject.movietheater.domain.exception.DBException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public interface Command {
    void execute(HttpServletRequest req,
                   HttpServletResponse resp) throws IOException, DBException;
}
