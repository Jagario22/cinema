package com.epam.finalproject.movietheater.web.filter;

import static com.epam.finalproject.movietheater.web.constants.Locale.ATTRIBUTE_NAME;

import com.epam.finalproject.movietheater.web.constants.Locale;
import com.epam.finalproject.movietheater.web.controller.Controller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "SessionLocaleFilter", urlPatterns = {"/*"})
public class SessionLocaleFilter implements Filter {
    private final static Logger log = LogManager.getLogger(Controller.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String language = Locale.RU;
        if (req.getParameter(ATTRIBUTE_NAME) != null) {
            language = req.getParameter(ATTRIBUTE_NAME);
        } else if (req.getSession().getAttribute(ATTRIBUTE_NAME) != null) {
            language = (String) req.getSession().getAttribute(ATTRIBUTE_NAME);
        }
        req.getSession().setAttribute(ATTRIBUTE_NAME, language);
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
