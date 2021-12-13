package com.epam.finalproject.cinema.web.filter;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.web.command.jsp.WelcomeCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.epam.finalproject.cinema.web.constants.path.FilterPath.WELCOME_PAGE_PATH;
/**
 * Filter for welcome page
 *
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
@WebFilter(filterName = "WelcomeFilter", urlPatterns = {WELCOME_PAGE_PATH})
public class WelcomeFilter implements Filter {
    private final static Logger log = LogManager.getLogger(WelcomeFilter.class);

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
