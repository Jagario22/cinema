package com.epam.finalproject.movietheater.web.command.login;

import com.epam.finalproject.movietheater.domain.entity.Film;
import com.epam.finalproject.movietheater.domain.entity.User;
import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.service.FilmService;
import com.epam.finalproject.movietheater.service.UserService;
import com.epam.finalproject.movietheater.web.constants.SessionAttributes;
import com.epam.finalproject.movietheater.web.command.jsp.PageCommand;
import com.epam.finalproject.movietheater.web.constants.PagePath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.epam.finalproject.movietheater.web.constants.SessionAttributes.MOVIE_LIST;

public class LoginCommand implements PageCommand {
    private final static Logger log = LogManager.getLogger(LoginCommand.class);
    private final UserService userService;
    private final static FilmService filmService = FilmService.getInstance();
    public LoginCommand() {
        this.userService = UserService.getInstance();
    }

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        log.debug("LoginCommand starts");

        HttpSession session = req.getSession();
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        String forward = PagePath.LOGIN_USER_PAGE;
        log.debug("login => " + login);
        User user;
        user = userService.findUserByLoginAndPassword(login, password);

        if (user != null) {
            session.setAttribute("user", user);
            log.debug("success login, user: " + user);
            List<Film> films = filmService.getAllCurrentFilms();
            session.setAttribute(MOVIE_LIST, films);
            forward ="";
        } else {
            req.getSession().setAttribute(SessionAttributes.SIGN_UP_ERROR, true);
        }

        return forward;
    }

}
