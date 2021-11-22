package com.epam.finalproject.movietheater.web.command.jsp;

import com.epam.finalproject.movietheater.domain.entity.Film;
import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.service.FilmService;
import com.epam.finalproject.movietheater.web.command.ShowMoviesCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.epam.finalproject.movietheater.web.constants.PagePath.WELCOME_PAGE;
import static com.epam.finalproject.movietheater.web.constants.SessionAttributes.MOVIE_LIST;

public class WelcomeCommand implements PageCommand {
    private final static FilmService filmService = FilmService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowMoviesCommand.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        HttpSession session = req.getSession();
        List<Film> films = filmService.getAllCurrentFilms();
        session.setAttribute(MOVIE_LIST, films);
        return WELCOME_PAGE;
    }
}
