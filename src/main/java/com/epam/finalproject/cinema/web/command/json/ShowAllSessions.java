package com.epam.finalproject.cinema.web.command.json;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.SessionService;
import com.epam.finalproject.cinema.web.model.film.session.SessionsInfoGroupByDate;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * This command is used for writing all sessions in response as a json
 *
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class ShowAllSessions implements Command {
    private final static SessionService sessionService = SessionService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowAllSessions.class);

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        String filmIdParam = req.getParameter("id");
        if (filmIdParam != null) {

            List<SessionsInfoGroupByDate> sessions = sessionService.getAllSessionsOfFilmGroupByDate(Integer.parseInt(filmIdParam));
            resp.getWriter().write(new Gson().toJson(sessions));
        }
    }
}
