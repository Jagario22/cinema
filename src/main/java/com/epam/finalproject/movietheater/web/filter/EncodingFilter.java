package com.epam.finalproject.movietheater.web.filter;

import com.epam.finalproject.movietheater.web.controller.Controller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(filterName = "EncodingFilter", urlPatterns = {"/*"})
public class EncodingFilter implements Filter {
    private final static Logger log = LogManager.getLogger(Controller.class);
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


        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        next.doFilter(request, response);
    }


    public void destroy() {
    }
}