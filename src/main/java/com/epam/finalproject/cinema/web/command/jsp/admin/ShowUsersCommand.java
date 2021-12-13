package com.epam.finalproject.cinema.web.command.jsp.admin;

import com.epam.finalproject.cinema.domain.user.User;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.SessionService;
import com.epam.finalproject.cinema.service.UserProfileService;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.web.constants.CinemaConstants;
import com.epam.finalproject.cinema.web.constants.path.Path;
import com.epam.finalproject.cinema.web.model.film.session.SessionInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ShowUsersCommand implements PageCommand {
    private final static UserProfileService userProfileService = UserProfileService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowUsersCommand.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException, ServletException {
        List<User> users = userProfileService.getAllUsers();
        req.getSession().setAttribute("users", users);
        req.getSession().setAttribute("roles", CinemaConstants.roles);
        return Path.ADMIN_MANAGE_USERS_PAGE;
    }
}
