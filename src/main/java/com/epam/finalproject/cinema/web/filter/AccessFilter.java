package com.epam.finalproject.cinema.web.filter;

import com.epam.finalproject.cinema.web.constants.ErrorMessages;
import com.epam.finalproject.cinema.web.constants.Locale;
import com.epam.finalproject.cinema.web.constants.PagePath;
import com.epam.finalproject.cinema.web.controller.Controller;
import com.epam.finalproject.cinema.web.model.user.UserProfileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.epam.finalproject.cinema.domain.entity.User.ROLE.ADMIN;
import static com.epam.finalproject.cinema.domain.entity.User.ROLE.USER;
import static com.epam.finalproject.cinema.web.constants.FilterPath.USERS_PATH;


@WebFilter(filterName = "AccessFilter", urlPatterns = {USERS_PATH})
public class AccessFilter implements Filter {
    private final static Logger log = LogManager.getLogger(AccessFilter.class);
    private static String userPackagePath;
    private static String adminPackagePath;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        userPackagePath = "/user/";
        adminPackagePath = "/admin/";
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        log.debug("AccessFilter doFilter");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        UserProfileInfo sessionUser = (UserProfileInfo) req.getSession().getAttribute("user");
        log.debug("sessionUser: " + sessionUser);
        if (sessionUser != null) {
            String url = req.getRequestURL().toString();
            if (isAdminPage(url)) {
                if (!sessionUser.getRole().equals(ADMIN)) {
                    HttpServletResponse res = (HttpServletResponse) servletResponse;
                    res.sendRedirect(req.getContextPath() + "/" + PagePath.LOGIN_USER_PAGE);
                    return;
                }
            } else if (isUserPage(url)) {
                if (!sessionUser.getRole().equals(USER)) {
                    HttpServletResponse res = (HttpServletResponse) servletResponse;
                    res.sendRedirect(req.getContextPath() + "/" + PagePath.LOGIN_USER_PAGE);
                    return;
                }
            }
        } else {
            HttpServletResponse res = (HttpServletResponse) servletResponse;
            res.sendRedirect(req.getContextPath() + "/" + PagePath.LOGIN_USER_PAGE);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isUserPage(String url) {
        return url.contains(userPackagePath);
    }

    private boolean isAdminPage(String url) {
        return url.contains(adminPackagePath);
    }

    private void errorPageForward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {


    }
}
