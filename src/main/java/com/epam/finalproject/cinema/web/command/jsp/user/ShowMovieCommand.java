package com.epam.finalproject.cinema.web.command.jsp.user;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.FilmService;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.web.model.film.FilmInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.epam.finalproject.cinema.web.constants.path.Path.*;

public class ShowMovieCommand implements PageCommand {
    private FilmService filmService = FilmService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowMovieCommand.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        String page = WELCOME_PAGE;
        String filmIdParam = req.getParameter("id");
        if (filmIdParam != null) {
            page = MOVIE_INFO_PAGE;
            FilmInfo film = filmService.getById(Integer.parseInt(filmIdParam));
            req.setAttribute("filmInfo", film);
        }

        return page;
    }

    public void setFilmService(FilmService filmService) {
        this.filmService = filmService;
    }
}
