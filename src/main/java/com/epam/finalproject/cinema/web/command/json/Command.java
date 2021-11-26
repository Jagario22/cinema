package com.epam.finalproject.cinema.web.command.json;

import com.epam.finalproject.cinema.exception.DBException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public interface Command {
    void execute(HttpServletRequest req,
                   HttpServletResponse resp) throws IOException, DBException;
}
