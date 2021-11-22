package com.epam.finalproject.movietheater.web.controller;

import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.web.command.Command;
import com.epam.finalproject.movietheater.web.command.CommandContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.epam.finalproject.movietheater.web.constants.CommandNames.WELCOME_PAGE_COMMAND;

@WebServlet(name = "movie-page", value = "/movie")
public class MovieController extends HttpServlet {
    private final static Logger log = LogManager.getLogger(MovieController.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.debug("doGet#");
        try {
            processCommand(req, resp);
        } catch (DBException e) {
            resp.setContentType("text/html; charset=UTF-8");
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void processCommand(HttpServletRequest req, HttpServletResponse resp) throws DBException, IOException {
        String commandName = req.getParameter("command");
        if (commandName == null || commandName.isEmpty()) {
            commandName = WELCOME_PAGE_COMMAND;
        }
        Command command = CommandContainer.getCommand(commandName);
        command.execute(req, resp);
    }
}
