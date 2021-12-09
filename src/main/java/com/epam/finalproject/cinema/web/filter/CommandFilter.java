package com.epam.finalproject.cinema.web.filter;

import com.epam.finalproject.cinema.domain.user.User;
import com.epam.finalproject.cinema.web.constants.CommandNames;
import com.epam.finalproject.cinema.web.constants.path.Path;
import com.epam.finalproject.cinema.web.model.user.UserProfileInfo;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.epam.finalproject.cinema.web.constants.path.FilterPath.ALL_PAGES;

@WebFilter(filterName = "CommandFilter", urlPatterns = {ALL_PAGES})
public class CommandFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        if (req.getParameter("command") != null) {
            if (isNotAccessibleCommand(req, resp)) {
                resp.sendRedirect(req.getContextPath() + "/" + Path.LOGIN_USER_PAGE);
                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isNotAccessibleCommand(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (req.getParameter("command") != null) {
            String value = req.getParameter("command");
            if (isAdminCommand(value)) {
                return !isAdmin(req);
            }
        }
        return false;
    }

    private boolean isAdmin(HttpServletRequest req) throws IOException {
        if (req.getSession().getAttribute("user") != null) {
            UserProfileInfo user = (UserProfileInfo) req.getSession().getAttribute("user");
            return user.getRole().equals(User.ROLE.ADMIN);
        }
        return false;
    }

    private boolean isAdminCommand(String value) {
        return value.equals(CommandNames.ADMIN_SHOW_ALL_MOVIES_INFO) || value.equals(CommandNames.ADMIN_SHOW_SCHEDULE);
    }
}
