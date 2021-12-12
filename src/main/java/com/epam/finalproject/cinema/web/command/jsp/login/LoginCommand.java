package com.epam.finalproject.cinema.web.command.jsp.login;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.FilmService;
import com.epam.finalproject.cinema.service.UserProfileService;
import com.epam.finalproject.cinema.web.constants.SessionAttributes;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.web.constants.path.Path;
import com.epam.finalproject.cinema.web.model.user.UserProfileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.epam.finalproject.cinema.web.constants.path.Path.WELCOME_PAGE;

public class LoginCommand implements PageCommand {
    private final static Logger log = LogManager.getLogger(LoginCommand.class);
    private final UserProfileService userProfileService;
    private final static FilmService filmService = FilmService.getInstance();
    public LoginCommand() {
        this.userProfileService = UserProfileService.getInstance();
    }

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        log.debug("LoginCommand starts");

        HttpSession session = req.getSession();
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        String forward = Path.LOGIN_USER_PAGE;
        log.debug("login => " + login);
        UserProfileInfo user;
        user = userProfileService.findUserByLoginAndPassword(login, password);

        if (user != null) {
            session.setAttribute("user", user);
            log.debug("success login, user: " + user);
            forward = WELCOME_PAGE;
        } else {
            req.getSession().setAttribute(SessionAttributes.SIGN_UP_ERROR, true);
        }

        return forward;
    }

}
