package com.epam.finalproject.cinema.web.command.jsp;

import com.epam.finalproject.cinema.domain.entity.Film;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.FilmService;
import com.epam.finalproject.cinema.util.Pagination;
import com.epam.finalproject.cinema.web.constants.CinemaConstants;
import com.epam.finalproject.cinema.web.constants.Params;
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
    private final static Logger log = LogManager.getLogger(ShowMovieCommand.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        HttpSession session = req.getSession();

        int page = Pagination.extractPage(req);
        int pageSize = Pagination.extractSize(req);

        String sortField = Params.DATE_TIME_FIELD;
        if (req.getParameter(Params.SORT_FIELD) != null)
            sortField = req.getParameter(Params.SORT_FIELD);
        int size = filmService.getCountOfCurrentFilms();
        List<Film> films = filmService.getAllCurrentFilmsSortedBy(sortField,
                pageSize * (page - 1), pageSize);

        if (films.size() != 0) {
            session.setAttribute(MOVIE_LIST, films);
        }
        req.getSession().setAttribute("countOfRows", CinemaConstants.COUNT_OF_ROWS);
        req.getSession().setAttribute("countOfRowSeats", CinemaConstants.COUNT_OF_ROW_SEAT);
        req.setAttribute(Params.SORT_FIELD, sortField);
        Pagination.setUpAttributes(req, page, pageSize, size);
        return WELCOME_PAGE;
    }
}
