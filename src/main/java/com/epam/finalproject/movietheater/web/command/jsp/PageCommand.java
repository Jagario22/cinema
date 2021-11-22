package com.epam.finalproject.movietheater.web.command.jsp;

import com.epam.finalproject.movietheater.domain.exception.DBException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface PageCommand {
    String execute(HttpServletRequest req,
                   HttpServletResponse resp) throws IOException, DBException;
}
