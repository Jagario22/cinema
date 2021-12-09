package com.epam.finalproject.cinema.web.command.jsp.admin;

import com.epam.finalproject.cinema.exception.BadRequestException;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.FilmService;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.web.constants.path.Path;
import com.epam.finalproject.cinema.web.model.film.FilmInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ShowAllMoviesInfoCommand implements PageCommand {
    private final static FilmService filmService = FilmService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowAllMoviesInfoCommand.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException, BadRequestException, ServletException {
        List<FilmInfo> films = filmService.getAllFilms();
        req.getSession().setAttribute("films", films);

        return Path.ADMIN_MOVIES_INFO_PAGE;
    }
}
