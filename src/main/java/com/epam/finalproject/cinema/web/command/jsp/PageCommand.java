package com.epam.finalproject.cinema.web.command.jsp;

import com.epam.finalproject.cinema.exception.DBException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * Interface provides method for execution command that is used for getting address to forward or redirect
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public interface PageCommand {
    String execute(HttpServletRequest req,
                   HttpServletResponse resp) throws IOException, DBException, ServletException;
}
