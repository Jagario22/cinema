package com.epam.finalproject.cinema.web.command.jsp;

import com.epam.finalproject.cinema.domain.entity.Film;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.FilmService;
import com.epam.finalproject.cinema.service.UserProfileService;
import com.epam.finalproject.cinema.web.constants.CinemaConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.epam.finalproject.cinema.web.constants.PagePath.WELCOME_PAGE;
import static com.epam.finalproject.cinema.web.constants.SessionAttributes.MOVIE_LIST;

public class WelcomeCommand implements PageCommand {
    private final static FilmService filmService = FilmService.getInstance();
    private final static UserProfileService USER_PROFILE_SERVICE = UserProfileService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowMovieCommand.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        HttpSession session = req.getSession();
        List<Film> films = filmService.getAllCurrentFilms();
        if (films.size() != 0) {
            session.setAttribute(MOVIE_LIST, films);
        }
        req.getSession().setAttribute("countOfRows", CinemaConstants.COUNT_OF_ROWS);
        req.getSession().setAttribute("countOfRowSeats", CinemaConstants.COUNT_OF_ROW_SEAT);

      /*  if (session.getAttribute("user") == null) {
            UserProfileInfo userProfileInfo = userService.findUserByLoginAndPassword("user3",
                    "Password1&");
            session.setAttribute("user", userProfileInfo);
        }*/
        return WELCOME_PAGE;
    }
}
