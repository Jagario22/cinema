package com.epam.finalproject.cinema.web.command.jsp.admin;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.SessionService;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.web.constants.path.Path;
import com.epam.finalproject.cinema.web.model.film.session.SessionInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ShowAllSessionsInfoCommand implements PageCommand {
    private final static SessionService sessionService = SessionService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowAllSessionsInfoCommand.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException, ServletException {
        List<SessionInfo> sessions = sessionService.getAllCurrentSessionsInfo();
        req.getSession().setAttribute("sessions", sessions);
        return Path.ADMIN_FILM_SESSIONS_INFO_PAGE;
    }
}
