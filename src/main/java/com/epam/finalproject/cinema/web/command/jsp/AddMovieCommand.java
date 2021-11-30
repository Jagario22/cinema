package com.epam.finalproject.cinema.web.command.jsp;

import com.epam.finalproject.cinema.domain.entity.Film;
import com.epam.finalproject.cinema.exception.BadRequestException;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.FilmService;
import com.epam.finalproject.cinema.web.model.film.FilmInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AddMovieCommand implements PageCommand {
    private final static FilmService filmService = FilmService.getInstance();
    private final static Logger log = LogManager.getLogger(AddMovieCommand.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException, BadRequestException {
        FilmInfo filmInfo = readFilmInfo(req);

        filmService.create(filmInfo);
        return new ShowAddMoviePage().execute(req, resp);
    }

    public FilmInfo readFilmInfo(HttpServletRequest req) {
        String img = req.getParameter("img");
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String yearProd = req.getParameter("year_prod");
        String len = req.getParameter("len");
        String category = req.getParameter("category");
        String rating = req.getParameter("rating");
        String[] genresId = req.getParameterValues("genre");

        return null;
    }

    public boolean validateFilmInfo(FilmInfo filmInfo, HttpServletRequest request) {


        return true;
    }
}
