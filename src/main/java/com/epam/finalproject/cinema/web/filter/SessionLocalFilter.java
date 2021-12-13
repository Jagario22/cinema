package com.epam.finalproject.cinema.web.filter;

import static com.epam.finalproject.cinema.web.constants.path.FilterPath.ALL_PAGES;
import static com.epam.finalproject.cinema.web.constants.Locale.LANG_ATTRIBUTE_NAME;

import com.epam.finalproject.cinema.web.constants.Locale;
import com.epam.finalproject.cinema.web.controller.Controller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
/**
 * Filter for user local language which stores in session
 *
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
@WebFilter(filterName = "SessionLocalFilter", urlPatterns = {ALL_PAGES})
public class SessionLocalFilter implements Filter {
    private final static Logger log = LogManager.getLogger(SessionLocalFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.debug("session local filter");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String language = Locale.RU;
        if (req.getParameter(LANG_ATTRIBUTE_NAME) != null) {
            language = req.getParameter(LANG_ATTRIBUTE_NAME);
        } else if (req.getSession().getAttribute(LANG_ATTRIBUTE_NAME) != null) {
            language = (String) req.getSession().getAttribute(LANG_ATTRIBUTE_NAME);
        }
        req.getSession().setAttribute(LANG_ATTRIBUTE_NAME, language);
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
