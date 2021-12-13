package com.epam.finalproject.cinema.web.command.json;

import com.epam.finalproject.cinema.exception.DBException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Interface provides method for execution command that is used for writing json for response
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public interface Command {
    void execute(HttpServletRequest req,
                   HttpServletResponse resp) throws IOException, DBException;



}
