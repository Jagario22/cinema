package com.epam.finalproject.cinema.web.command.jsp;

import com.epam.finalproject.cinema.exception.BadRequestException;
import com.epam.finalproject.cinema.exception.DBException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface PageCommand {
    String execute(HttpServletRequest req,
                   HttpServletResponse resp) throws IOException, DBException, BadRequestException, ServletException;
}
