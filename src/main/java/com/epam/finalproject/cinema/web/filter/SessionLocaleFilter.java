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

@WebFilter(filterName = "SessionLocaleFilter", urlPatterns = {ALL_PAGES})
public class SessionLocaleFilter implements Filter {
    private final static Logger log = LogManager.getLogger(Controller.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
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
