package com.epam.finalproject.movietheater.web.controller;

import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.web.command.json.JsonCommand;
import com.epam.finalproject.movietheater.web.command.json.JsonCommandContainer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.epam.finalproject.movietheater.web.constants.CommandNames.SHOW_MOVIES;

@WebServlet(name = "movie-page", value = "/movie")
public class MovieController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
            commandName = SHOW_MOVIES;
        }
        JsonCommand command = JsonCommandContainer.getCommand(commandName);
        command.execute(req, resp);
    }
}
