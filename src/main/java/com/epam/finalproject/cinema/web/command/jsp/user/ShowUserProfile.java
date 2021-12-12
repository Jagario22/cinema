package com.epam.finalproject.cinema.web.command.jsp.user;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.TicketService;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.web.constants.path.Path;
import com.epam.finalproject.cinema.web.model.ticket.TicketInfo;
import com.epam.finalproject.cinema.web.model.user.UserProfileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class ShowUserProfile implements PageCommand {
    private  TicketService ticketService =
            TicketService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowUserProfile.class);
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        HttpSession session = req.getSession();

        if (session.getAttribute("user") != null) {
            UserProfileInfo user = (UserProfileInfo) session.getAttribute("user");
            List<TicketInfo> userTickets = ticketService.getTicketsInfoByUserId(user.getId());
            session.setAttribute("userTickets", userTickets);
        }

        return Path.USER_PROFILE_PAGE;
    }

    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }
}
