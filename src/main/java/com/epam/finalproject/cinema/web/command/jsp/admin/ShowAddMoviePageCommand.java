package com.epam.finalproject.cinema.web.command.jsp.admin;

import com.epam.finalproject.cinema.domain.Genre.Genre;
import com.epam.finalproject.cinema.domain.user.User;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.GenreService;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.web.command.jsp.WelcomeCommand;
import com.epam.finalproject.cinema.web.constants.path.Path;
import com.epam.finalproject.cinema.web.constants.SessionAttributes;
import com.epam.finalproject.cinema.web.model.user.UserProfileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
/**
 * Command for admins when they want to get add movie page
 *
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class ShowAddMoviePageCommand implements PageCommand {
    private final static GenreService genreService = GenreService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowAddMoviePageCommand.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        if (req.getSession().getAttribute("user") != null) {
            UserProfileInfo user = (UserProfileInfo) req.getSession().getAttribute("user");
            if (user.getRole().equals(User.ROLE.USER))
                return new WelcomeCommand().execute(req, resp);
        } else {
            return Path.LOGIN_USER_PAGE;
        }
        List<Genre> genres = genreService.getAllGenres();
        req.setAttribute(SessionAttributes.GENRES, genres);
        return Path.ADD_MOVIE_PAGE;
    }
}
