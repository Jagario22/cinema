package com.epam.finalproject.movietheater.web.filter;

import com.epam.finalproject.movietheater.domain.entity.Film;
import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.service.FilmService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.epam.finalproject.movietheater.web.constants.FilterPath.WELCOME_PAGE_PATH;
import static com.epam.finalproject.movietheater.web.constants.SessionAttributes.MOVIE_LIST;

@WebFilter(filterName = "MovieFilter", urlPatterns = {WELCOME_PAGE_PATH})
public class MovieFilter implements Filter {
    private final static FilmService filmService = FilmService.getInstance();
    private final static Logger log = LogManager.getLogger(MovieFilter.class);
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpSession session = req.getSession();
        try {
            List<Film> films = filmService.getAllCurrentFilms();
            if (films.size() != 0) {
                session.setAttribute(MOVIE_LIST, films);
            }
        } catch (DBException e) {
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
