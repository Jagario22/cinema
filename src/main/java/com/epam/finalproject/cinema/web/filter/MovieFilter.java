package com.epam.finalproject.cinema.web.filter;

import com.epam.finalproject.cinema.domain.entity.Film;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.FilmService;
import com.epam.finalproject.cinema.util.Pagination;
import com.epam.finalproject.cinema.web.command.jsp.WelcomeCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.epam.finalproject.cinema.web.constants.FilterPath.WELCOME_PAGE_PATH;
import static com.epam.finalproject.cinema.web.constants.SessionAttributes.MOVIE_LIST;

@WebFilter(filterName = "MovieFilter", urlPatterns = {WELCOME_PAGE_PATH})
public class MovieFilter implements Filter {
    private final static Logger log = LogManager.getLogger(MovieFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            new WelcomeCommand().execute((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
        } catch (DBException e) {
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
