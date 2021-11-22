package com.epam.finalproject.movietheater.web.command;

import com.epam.finalproject.movietheater.domain.entity.Film;
import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.service.FilmService;
import com.epam.finalproject.movietheater.web.command.jsp.PageCommand;
import com.epam.finalproject.movietheater.web.model.FilmDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.epam.finalproject.movietheater.web.constants.PagePath.*;
import static com.epam.finalproject.movietheater.web.constants.SessionAttributes.MOVIE_LIST;

public class ShowMoviesCommand implements PageCommand {
    private final static FilmService filmService = FilmService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowMoviesCommand.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String currentPage = WELCOME_PAGE;

        try {
            HttpSession session = req.getSession();
            if (session.getAttribute("user") != null) {
                currentPage = WELCOME_PAGE;
            }
            String filmIdParam = req.getParameter("id");

            if (filmIdParam != null) {
                currentPage = MOVIE_INFO_PAGE;
                FilmDTO film = filmService.getById(Integer.parseInt(filmIdParam));
                req.setAttribute("filmInfo", film);
            } else {
                List<Film> films = filmService.getAllCurrentFilms();
                session.setAttribute(MOVIE_LIST, films);

            }
        } catch (DBException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return currentPage;
    }
}
