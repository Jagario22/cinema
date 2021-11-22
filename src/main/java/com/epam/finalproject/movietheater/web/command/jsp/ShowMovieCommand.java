package com.epam.finalproject.movietheater.web.command.jsp;

import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.service.FilmService;
import com.epam.finalproject.movietheater.web.model.FilmDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.epam.finalproject.movietheater.web.constants.PagePath.*;

public class ShowMovieCommand implements PageCommand {
    private final static FilmService filmService = FilmService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowMovieCommand.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        String currentPage = WELCOME_PAGE;
        String filmIdParam = req.getParameter("id");
        if (filmIdParam != null) {
            currentPage = MOVIE_INFO_PAGE;
            FilmDTO film = filmService.getById(Integer.parseInt(filmIdParam));
            req.setAttribute("filmInfo", film);
        }

        return currentPage;
    }
}
