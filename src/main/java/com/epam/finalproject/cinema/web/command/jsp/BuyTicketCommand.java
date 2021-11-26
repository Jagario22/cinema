package com.epam.finalproject.cinema.web.command.jsp;

import com.epam.finalproject.cinema.domain.entity.Wallet;
import com.epam.finalproject.cinema.exception.BadRequestException;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.exception.purchase.InactiveFilmSessionException;
import com.epam.finalproject.cinema.exception.purchase.InsufficientBalanceException;
import com.epam.finalproject.cinema.service.TicketService;
import com.epam.finalproject.cinema.service.UserProfileService;
import com.epam.finalproject.cinema.web.constants.ErrorMessages;
import com.epam.finalproject.cinema.web.constants.PagePath;
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
            throw new BadRequestException(ErrorMessages.BAD_REQUEST);
        }

        try {
            ticketService.processTicketPurchase(Integer.parseInt(ticketId), Integer.parseInt(sessionId),
                    Integer.parseInt(userId));
            log.debug("ticket purchase succeed");
            req.getSession().setAttribute(SessionAttributes.SUCCESS_TICKET_PURCHASE, true);
            UserProfileInfo user = (UserProfileInfo) req.getSession().getAttribute("user");
            user.getWallet().setBalance(userService.getWalletBalanceByUserId(user.getId()));
            req.getSession().setAttribute("user", user);
        } catch (InactiveFilmSessionException | InsufficientBalanceException e) {
            log.debug(e.getMessage());
            req.getSession().setAttribute(SessionAttributes.TICKET_PURCHASE_ERROR, e.getMessage());
        }

        return new ShowUserProfile().execute(req, resp);
    }


    private String getCurrentUrl(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append(req.getRequestURL())
                .append("?")
                .append("command=").append(req.getParameter("command"))
                .append("&filmId=").append(req.getParameter("filmId"));

        return sb.toString();
    }
}
