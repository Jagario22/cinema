package com.epam.finalproject.movietheater.web.command.json;

import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.service.FilmService;
import com.epam.finalproject.movietheater.service.SessionService;
import com.epam.finalproject.movietheater.web.model.FilmDTO;
import com.epam.finalproject.movietheater.web.model.SessionDTO;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;


public class ShowAllSessions implements JsonCommand {
    private final static SessionService sessionService = SessionService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowAllSessions.class);

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        String filmIdParam = req.getParameter("id");
        if (filmIdParam != null) {

            List<SessionDTO> sessions = sessionService.getAllSessionsOfFilm(Integer.parseInt(filmIdParam));
            resp.getWriter().write(new Gson().toJson(sessions));
        }
    }
}