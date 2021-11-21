package com.epam.finalproject.movietheater.web.command.jsp.login;

import com.epam.finalproject.movietheater.domain.dao.UserDao;
import com.epam.finalproject.movietheater.domain.entity.User;
import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.web.util.ValidationUtil;
import com.epam.finalproject.movietheater.web.command.jsp.Command;
import com.epam.finalproject.movietheater.web.constants.PagePath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.epam.finalproject.movietheater.web.constants.SessionAttributes.*;

public class RegisterCommand implements Command {
    private final static Logger log = LogManager.getLogger(Command.class);

    private final UserDao userDao;

    public RegisterCommand() {
        this.userDao = UserDao.getInstance();
    }

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        log.debug("RegisterCommand starts");
        HttpSession session = req.getSession();
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String forward = PagePath.LOGIN_USER_PAGE;

        if (!isValidUserForm(login, password, email)) {
            req.getSession().setAttribute(VALIDATING_STATUS, false);
            return PagePath.REGISTER_USER_PAGE;
        }

        req.getSession().setAttribute(UNIQUE_EMAIL_VALIDATION_CLASS, "");
        req.getSession().setAttribute(UNIQUE_LOGIN_VALIDATION_CLASS, "");
        try {
            if (!validateUniqueData(login, email, req)) {
                return PagePath.REGISTER_USER_PAGE;
            }
        } catch (DBException e) {
            return PagePath.ERROR_PAGE;
        }

        User user = new User(email, password, login, User.ROLE.USER);
        int id;
        try {
            id = insertUser(user);
        } catch (DBException e) {
            return PagePath.ERROR_PAGE;
        }
        user.setId(id);
        req.getSession().setAttribute(SUCCESS_REGISTRATION, true);
        return forward;
    }

    private boolean isValidUserForm(String login, String password, String email) {
        return ValidationUtil.isValidLogin(login) && ValidationUtil.isValidEmail(email)
                && ValidationUtil.isValidPassword(password);
    }

    private boolean validateUniqueData(String login, String email, HttpServletRequest req) throws DBException {
        List<User> users = userDao.findUsersWithEqualLoginOrEmail(login, email);
        if (users.size() == 0) {
            return true;
        }
        if (users.size() > 2) {
            throw new IllegalStateException("users size with equal login or password is bigger than 1");
        }
        boolean result = true;
        for (User user : users) {
            if (user.getEmail() != null) {
                req.getSession().setAttribute(UNIQUE_EMAIL_VALIDATION_CLASS, "is-invalid");
                result = false;
            }
            if (user.getLogin() != null) {
                req.getSession().setAttribute(UNIQUE_LOGIN_VALIDATION_CLASS, "is-invalid");
                result = false;
            }
        }


        return result;
    }

    private int insertUser(User user) throws DBException {
        int id;
        id = userDao.insertUser(user);
        user.setId(id);
        return id;
    }
}
