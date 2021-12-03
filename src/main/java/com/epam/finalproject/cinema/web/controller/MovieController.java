package com.epam.finalproject.cinema.web.controller;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.web.command.json.Command;
import com.epam.finalproject.cinema.web.command.json.CommandContainer;
import com.epam.finalproject.cinema.web.constants.path.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.epam.finalproject.cinema.web.constants.CommandNames.WELCOME_PAGE_COMMAND;

@WebServlet(name = "movie-page", value = "/movie")
public class MovieController extends HttpServlet {
    private final static Logger log = LogManager.getLogger(MovieController.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            processCommand(req, resp);
        } catch (DBException e) {
            throw new IOException(e.getMessage());
        } catch (IllegalArgumentException e) {
            processError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            processCommand(req, resp);
        } catch (DBException e) {
            throw new IOException(e.getMessage());
        } catch (IllegalArgumentException e) {
            processError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), req, resp);
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

    private void processError(int statusCode, String massage, HttpServletRequest request,
                              HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("javax.servlet.error.status_code",
                statusCode);
        request.setAttribute("javax.servlet.error.message",
                massage);
        request.getRequestDispatcher(Path.ERROR_PAGE).forward(request, response);
    }
}
