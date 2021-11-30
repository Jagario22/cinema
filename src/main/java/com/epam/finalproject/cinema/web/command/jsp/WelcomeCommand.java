package com.epam.finalproject.cinema.web.command.jsp;

import com.epam.finalproject.cinema.domain.entity.Film;
import com.epam.finalproject.cinema.domain.entity.User;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.FilmService;
import com.epam.finalproject.cinema.service.SessionService;
import com.epam.finalproject.cinema.util.Pagination;
import com.epam.finalproject.cinema.web.constants.CinemaConstants;
import com.epam.finalproject.cinema.web.constants.Params;
import com.epam.finalproject.cinema.web.model.film.FilmStatistic;
import com.epam.finalproject.cinema.web.model.film.session.SessionsInfoGroupByDate;
import com.epam.finalproject.cinema.web.model.user.UserProfileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.epam.finalproject.cinema.web.constants.PagePath.WELCOME_PAGE;
import static com.epam.finalproject.cinema.web.constants.SessionAttributes.*;

public class WelcomeCommand implements PageCommand {
    private final static FilmService filmService = FilmService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowMovieCommand.class);


    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {

        if (req.getSession().getAttribute("user") != null) {
            UserProfileInfo user = (UserProfileInfo) req.getSession().getAttribute("user");
            if (user.getRole().equals(User.ROLE.USER)) {
                executeWelcomeToUser(req);
            } else {
                executeWelcomeToAdmin(req);
            }
        } else {
            executeWelcomeToUser(req);
        }

        return WELCOME_PAGE;
    }

    private void executeWelcomeToAdmin(HttpServletRequest req) throws DBException {
        int page = Pagination.extractPage(req);
        int pageSize = Pagination.extractSize(req);
        LocalDateTime now = LocalDateTime.now();
        int size = filmService.getAllCurrentFilmsCount(Params.DATE_TIME_FIELD, now, null);
        List<FilmStatistic> films;
        if (size != 0) {
            films = filmService.getCurrentFilmsStatistic(pageSize * (page - 1), pageSize);
            req.getSession().setAttribute("filmsStatistic", films);
        }
    }

    private void executeWelcomeToUser(HttpServletRequest req) throws DBException {
        HttpSession session = req.getSession();
        int page = Pagination.extractPage(req);
        int pageSize = Pagination.extractSize(req);

        String sortField = Params.DATE_TIME_FIELD;
        if (req.getParameter(Params.SORT_FIELD) != null)
            sortField = req.getParameter(Params.SORT_FIELD);

        LocalDateTime startDateTime = getStartLocalTime(req);
        LocalDateTime endDateTime = getEndLocalDateTime(req);
        if (endDateTime != null && !endDateTime.isAfter(startDateTime)) {
            endDateTime = null;
        }
        int size = filmService.getAllCurrentFilmsCount(sortField, startDateTime, endDateTime);
        List<Film> films;
        if (size != 0) {
            films = filmService.getAllCurrentFilmsSortedByAndBetween(sortField, startDateTime, endDateTime,
                    pageSize * (page - 1), pageSize);
            session.setAttribute(MOVIE_LIST, films);
        } else {
            session.removeAttribute(START_DATE_TIME);
            session.removeAttribute(END_DATE_TIME);
            session.removeAttribute(MOVIE_LIST);
        }

        req.getSession().setAttribute("countOfRows", CinemaConstants.COUNT_OF_ROWS);
        req.getSession().setAttribute("countOfRowSeats", CinemaConstants.COUNT_OF_ROW_SEAT);
        req.setAttribute(Params.SORT_FIELD, sortField);

        Pagination.setUpAttributes(req, page, pageSize, size);
    }

    private LocalDateTime getStartLocalTime(HttpServletRequest req) {
        if (req.getParameter(START_DATE_TIME) != null) {
            LocalDateTime result = LocalDateTime.parse(req.getParameter(START_DATE_TIME));
            req.getSession().setAttribute(START_DATE_TIME, req.getParameter(START_DATE_TIME));
            return result;
        } else if (req.getSession().getAttribute(START_DATE_TIME) != null) {
            return LocalDateTime.parse((String) req.getSession().getAttribute(START_DATE_TIME));
        } else {
            return LocalDateTime.now();
        }
    }

    private LocalDateTime getEndLocalDateTime(HttpServletRequest req) {
        if (req.getParameter(END_DATE_TIME) != null) {
            LocalDateTime result = LocalDateTime.parse(req.getParameter(END_DATE_TIME));
            req.getSession().setAttribute(END_DATE_TIME, req.getParameter(END_DATE_TIME));
            return result;
        } else if (req.getSession().getAttribute(END_DATE_TIME) != null) {
            return LocalDateTime.parse((String) req.getSession().getAttribute(END_DATE_TIME));
        } else {
            return null;
        }
    }
}
