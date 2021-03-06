package com.epam.finalproject.cinema.web.command.jsp.login;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.web.command.jsp.WelcomeCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
/**
 * Command for users when they want to log out
 *
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class LogoutCommand implements PageCommand {
    private final static Logger log = LogManager.getLogger(LogoutCommand.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        HttpSession session = req.getSession();
        session.invalidate();
        log.debug("session is invalidate");
        return new WelcomeCommand().execute(req, resp);
    }
}
