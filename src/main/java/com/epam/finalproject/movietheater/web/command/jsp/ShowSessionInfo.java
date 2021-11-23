package com.epam.finalproject.movietheater.web.command.jsp;

import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.service.SessionService;
import com.epam.finalproject.movietheater.web.constants.CinemaConstants;
import com.epam.finalproject.movietheater.web.model.session.SessionInfo;
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
                SessionInfo sessionInfo = sessionService.getSessionById(Integer.parseInt(sessionId));
                req.getSession().setAttribute("sessionInfo", sessionInfo);
                req.getSession().setAttribute("countOfRows", CinemaConstants.COUNT_OF_ROWS);
                req.getSession().setAttribute("countOfRowSeats", CinemaConstants.COUNT_OF_ROW_SEAT);
            } else {
                throw new IOException("sessionId can't be null");
            }

        }
        return forward;
    }
}
