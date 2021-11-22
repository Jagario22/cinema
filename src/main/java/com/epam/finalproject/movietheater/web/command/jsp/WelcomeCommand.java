package com.epam.finalproject.movietheater.web.command.jsp;

import com.epam.finalproject.movietheater.domain.entity.Film;
import com.epam.finalproject.movietheater.domain.entity.User;
import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.service.FilmService;
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
    private final static Logger log = LogManager.getLogger(ShowMovieCommand.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        HttpSession session = req.getSession();
        List<Film> films = filmService.getAllCurrentFilms();
        if (films.size() != 0) {
            session.setAttribute(MOVIE_LIST, films);
        }


        if (session.getAttribute("user") == null) {
            User user = new User();
            user.setId(1);
            user.setLogin("user1");
            user.setEmail("user@gmail.com");
            user.setPassword("Password1&");
            user.setRole(User.ROLE.USER);
            session.setAttribute("user", user);
        }
        return WELCOME_PAGE;
    }
}
