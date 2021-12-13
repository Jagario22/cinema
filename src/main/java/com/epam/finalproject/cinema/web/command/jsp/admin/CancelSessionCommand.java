package com.epam.finalproject.cinema.web.command.jsp.admin;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.SessionService;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.web.constants.Params;
import com.epam.finalproject.cinema.web.constants.SessionAttributes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * Command for admins when they want to cancel session
 *
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class CancelSessionCommand implements PageCommand {
    private final static SessionService sessionService = SessionService.getInstance();
    private final static Logger log = LogManager.getLogger(CancelSessionCommand.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException, ServletException {
        String sessionId = req.getParameter(Params.SESSION_ID);

        try {
            if (sessionId != null) {
                sessionService.cancelSessionById(Integer.parseInt(sessionId));
            } else {
                req.getSession().setAttribute(SessionAttributes.INCORRECT_INPUT_DATA,
                        "The input data isn't full");
            }
        } catch (IllegalArgumentException e) {
            log.debug(e.getMessage());
            req.getSession().setAttribute(SessionAttributes.INCORRECT_INPUT_DATA, e.getMessage());
        }
        req.getSession().setAttribute(SessionAttributes.SUCCESS_MSG, "Canceling session was succeed");
        return new ShowAllSessionsInfoCommand().execute(req, resp);
    }
}
