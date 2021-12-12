package com.epam.finalproject.cinema.web.command.jsp.user;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.TicketService;
import com.epam.finalproject.cinema.service.UserProfileService;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.web.constants.ErrorMessages;
import com.epam.finalproject.cinema.web.constants.SessionAttributes;
import com.epam.finalproject.cinema.web.model.user.UserProfileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BuyTicketCommand implements PageCommand {
    private final static TicketService ticketService = TicketService.getInstance();
    private final static Logger log = LogManager.getLogger(BuyTicketCommand.class);
    private final static UserProfileService userService = UserProfileService.getInstance();

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        String ticketId = req.getParameter("ticketId");
        String sessionId = req.getParameter("sessionId");
        String userId = req.getParameter("userId");

        if (ticketId == null || sessionId == null || userId == null) {
            log.debug("params is null");
            throw new IllegalArgumentException(ErrorMessages.BAD_REQUEST);
        }

        try {
            ticketService.processTicketPurchase(Integer.parseInt(ticketId), Integer.parseInt(sessionId),
                    Integer.parseInt(userId));
            log.debug("ticket purchase succeed");
            req.getSession().setAttribute(SessionAttributes.SUCCESS_TICKET_PURCHASE, true);
            UserProfileInfo user = (UserProfileInfo) req.getSession().getAttribute("user");
            user.getWallet().setBalance(userService.getWalletBalanceByUserId(user.getId()));
            req.getSession().setAttribute("user", user);
        } catch (IllegalArgumentException e) {
            log.debug(e.getMessage());
            req.getSession().setAttribute(SessionAttributes.TICKET_PURCHASE_ERROR, e.getMessage());
        }

        return new ShowUserProfile().execute(req, resp);
    }
}
