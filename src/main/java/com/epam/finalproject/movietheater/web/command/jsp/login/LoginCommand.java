package com.epam.finalproject.movietheater.web.command.jsp.login;

import com.epam.finalproject.movietheater.domain.dao.UserDao;
import com.epam.finalproject.movietheater.domain.entity.User;
import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.web.constants.SessionAttributes;
import com.epam.finalproject.movietheater.web.command.jsp.Command;
import com.epam.finalproject.movietheater.web.constants.PagePath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginCommand implements Command {
    private final static Logger log = LogManager.getLogger(LoginCommand.class);
    private final UserDao userDao;

    public LoginCommand() {
        this.userDao = UserDao.getInstance();
    }

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.debug("LoginCommand starts");

        HttpSession session = req.getSession();
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        String forward = PagePath.LOGIN_USER_PAGE;
        log.debug("login => " + login);
        User user;

        try {
            user = userDao.findUserByLoginAndPassword(login, password);
        } catch (DBException ex) {
            return PagePath.ERROR_PAGE;
        }

        if (user != null) {
            final User.ROLE role = user.getRole();
            session.setAttribute("user", user);

            try {
                forward = getMainPageByRole(role);
            } catch (IllegalArgumentException ex) {
                return PagePath.ERROR_PAGE;
            }
        } else {
            log.debug("user wasn't found");
            req.getSession().setAttribute(SessionAttributes.SIGN_UP_ERROR, true);
        }

        return forward;
    }

    public String getMainPageByRole(User.ROLE role) {
        if (role.equals(User.ROLE.ADMIN)) {
            return PagePath.ADMIN_MAIN_PAGE;
        }

        if (role.equals(User.ROLE.USER)) {
            return PagePath.USER_MAIN_PAGE;
        }

        log.error(role + " is unknown");
        throw new IllegalArgumentException("Unknown user");
    }
}
