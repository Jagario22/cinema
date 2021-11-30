package com.epam.finalproject.cinema.web.command.login;

import com.epam.finalproject.cinema.domain.entity.User;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.UserProfileService;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.util.RegisterValidationUtil;
import com.epam.finalproject.cinema.web.constants.PagePath;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static com.epam.finalproject.cinema.web.constants.SessionAttributes.*;

public class RegisterCommand implements PageCommand {
    private final static Logger log = LogManager.getLogger(PageCommand.class);

    private final UserProfileService userProfileService;

    public RegisterCommand() {
        this.userProfileService = UserProfileService.getInstance();
    }

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        log.debug("RegisterCommand starts");
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
        if (!validateUniqueData(login, email, req)) {
            return PagePath.REGISTER_USER_PAGE;
        }

        User user = new User(email, password, login, User.ROLE.USER);
        int id;
        id = insertUser(user);

        user.setId(id);
        req.getSession().setAttribute(SUCCESS_REGISTRATION, true);
        return forward;
    }

    private boolean isValidUserForm(String login, String password, String email) {
        return RegisterValidationUtil.isValidLogin(login) && RegisterValidationUtil.isValidEmail(email)
                && RegisterValidationUtil.isValidPassword(password);
    }

    private boolean validateUniqueData(String login, String email, HttpServletRequest req) throws DBException {
        List<User> users = userProfileService.findUsersWithEqualLoginOrEmail(login, email);
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
        id = userProfileService.creatingUser(user);
        user.setId(id);
        return id;
    }
}
