package com.epam.finalproject.movietheater.web.command.jsp;

import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.service.FilmService;
import com.epam.finalproject.movietheater.service.SessionService;
import com.epam.finalproject.movietheater.web.constants.PagePath;
import com.epam.finalproject.movietheater.web.model.SessionDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.epam.finalproject.movietheater.web.constants.PagePath.LOGIN_USER_PAGE;
import static com.epam.finalproject.movietheater.web.constants.PagePath.SESSION_INFO_PAGE;

public class ShowSessionInfo implements PageCommand {
    private final static SessionService sessionService = SessionService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowSessionInfo.class);
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        String forward = SESSION_INFO_PAGE;

        if (req.getSession().getAttribute("user") == null) {
            forward = LOGIN_USER_PAGE;
        } else {
            String sessionId = req.getParameter("sessionId");
            if (sessionId != null) {
                SessionDTO sessionInfo = sessionService.getSessionById(Integer.parseInt(sessionId));
                req.getSession().setAttribute("sessionInfo", sessionInfo);
            } else {
                throw new IOException("sessionId can't be null");
            }

        }
        return forward;
    }
}
