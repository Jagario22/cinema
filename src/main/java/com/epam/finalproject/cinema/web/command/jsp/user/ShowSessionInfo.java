package com.epam.finalproject.cinema.web.command.jsp.user;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.SessionService;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.web.model.film.session.SessionInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.epam.finalproject.cinema.web.constants.path.Path.LOGIN_USER_PAGE;
import static com.epam.finalproject.cinema.web.constants.path.Path.SESSION_INFO_PAGE;

public class ShowSessionInfo implements PageCommand {
    private SessionService sessionService = SessionService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowSessionInfo.class);
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        String forward = SESSION_INFO_PAGE;

        if (req.getSession().getAttribute("user") == null) {
            forward = LOGIN_USER_PAGE;
        } else {
            String sessionId = req.getParameter("sessionId");
            if (sessionId != null) {
                SessionInfo sessionInfo = sessionService.getCurrentSessionInfoById(Integer.parseInt(sessionId));
                req.getSession().setAttribute("sessionInfo", sessionInfo);
            } else {
                throw new IOException("sessionId can't be null");
            }

        }
        return forward;
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }
}