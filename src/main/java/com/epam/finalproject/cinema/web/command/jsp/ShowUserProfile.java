package com.epam.finalproject.cinema.web.command.jsp;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.TicketService;
import com.epam.finalproject.cinema.web.constants.PagePath;
import com.epam.finalproject.cinema.web.model.ticket.TicketInfo;
import com.epam.finalproject.cinema.web.model.user.UserProfileInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

public class ShowUserProfile implements PageCommand {
    private final static TicketService ticketService =
            TicketService.getInstance();

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        HttpSession session = req.getSession();

        if (session.getAttribute("user") != null) {
            UserProfileInfo user = (UserProfileInfo) session.getAttribute("user");
            List<TicketInfo> userTickets = ticketService.getTicketsInfoByUserId(user.getId());
            session.setAttribute("userTickets", userTickets);
        }

        return PagePath.USER_PROFILE_PAGE;
    }
}
