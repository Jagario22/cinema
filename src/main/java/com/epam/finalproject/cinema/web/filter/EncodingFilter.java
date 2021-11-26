package com.epam.finalproject.cinema.web.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

import static com.epam.finalproject.cinema.web.constants.FilterPath.ALL_PAGES;

@WebFilter(filterName = "EncodingFilter", urlPatterns = {ALL_PAGES})
public class EncodingFilter implements Filter {
    private final static Logger log = LogManager.getLogger(EncodingFilter.class);
    private String encoding = "UTF-8";


    public void init(FilterConfig config) {
        if (config.getInitParameter("requestEncoding") != null)
            encoding = config.getInitParameter("requestEncoding");
    }

    /**
     * Set the default response content type and encoding
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain next)
            throws IOException, ServletException {

        if (null == request.getCharacterEncoding()) {
            request.setCharacterEncoding(encoding);
        }

        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        next.doFilter(request, response);
    }


    public void destroy() {
    }
}