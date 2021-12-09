package com.epam.finalproject.cinema.web.command.jsp.admin;

import com.epam.finalproject.cinema.domain.session.Session;
import com.epam.finalproject.cinema.exception.BadRequestException;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.exception.IncorrectInputDataException;
import com.epam.finalproject.cinema.service.SessionService;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.web.constants.SessionAttributes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import static com.epam.finalproject.cinema.web.constants.Params.*;
import static com.epam.finalproject.cinema.web.constants.SessionAttributes.SUCCESS_MSG;

public class AddSessionCommand implements PageCommand {
    private final static SessionService sessionService = SessionService.getInstance();
    private final static Logger log = LogManager.getLogger(AddSessionCommand.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException, BadRequestException, ServletException {
        Session session = readSession(req);
        if (sessionService != null) {
            try {
                sessionService.createSession(session);
            } catch (IncorrectInputDataException e) {
                log.debug(e.getMessage());
                req.getSession().setAttribute(SessionAttributes.INCORRECT_INPUT_DATA, e.getMessage());
                return new ShowAllSessionsInfoCommand().execute(req, resp);
            }
        }
        req.getSession().setAttribute(SUCCESS_MSG, "Session was added successfully");
        return new ShowAllSessionsInfoCommand().execute(req, resp);
    }

    private Session readSession(HttpServletRequest req) {
        String date = req.getParameter(SESSION_DATE);
        String time = req.getParameter(SESSION_TIME);
        String filmID = req.getParameter(FILM_ID);
        String sessionLang = req.getParameter(SESSION_LANG);

        if (!isFullData(date, time, filmID, sessionLang)) {
            req.getSession().setAttribute(SessionAttributes.NOT_FULL_INPUT_DATA_ERROR, true);
            return null;
        }

        return new Session(LocalDate.parse(date), LocalTime.parse(time), Integer.parseInt(filmID),
                Session.Lang.valueOf(sessionLang.toUpperCase()));
    }

    private boolean isFullData(String date, String time, String filmID, String sessionLang) {
        return date != null && time != null && filmID != null && sessionLang != null;
    }
}
