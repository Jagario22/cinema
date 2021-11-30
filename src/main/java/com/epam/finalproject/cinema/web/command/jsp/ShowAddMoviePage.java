package com.epam.finalproject.cinema.web.command.jsp;

import com.epam.finalproject.cinema.domain.entity.Genre;
import com.epam.finalproject.cinema.domain.entity.User;
import com.epam.finalproject.cinema.exception.BadRequestException;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.GenreService;
import com.epam.finalproject.cinema.web.command.login.LoginCommand;
import com.epam.finalproject.cinema.web.constants.PagePath;
import com.epam.finalproject.cinema.web.constants.SessionAttributes;
import com.epam.finalproject.cinema.web.model.user.UserProfileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ShowAddMoviePage implements PageCommand {
    private final static GenreService genreService = GenreService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowAddMoviePage.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException, BadRequestException {
        if (req.getSession().getAttribute("user") != null) {
            UserProfileInfo user = (UserProfileInfo) req.getSession().getAttribute("user");
            if (user.getRole().equals(User.ROLE.USER))
                return new WelcomeCommand().execute(req, resp);
        } else {
            return PagePath.LOGIN_USER_PAGE;
        }
        log.debug("show page");
        List<Genre> genres = genreService.getAllGenres();
        req.setAttribute(SessionAttributes.GENRES, genres);
        return PagePath.ADD_MOVIE_PAGE;
    }
}
