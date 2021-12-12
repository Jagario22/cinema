package com.epam.finalproject.cinema.web.command.jsp.user;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.UserProfileService;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.web.model.user.UserProfileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

public class TopUpBalanceCommand implements PageCommand {
    private final static Logger log = LogManager.getLogger(TopUpBalanceCommand.class);
    private UserProfileService userProfileService = UserProfileService.getInstance();

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        UserProfileInfo user = (UserProfileInfo) req.getSession().getAttribute("user");
        if (user == null) {
            throw new IllegalArgumentException("User wasn't found");
        }

        if (req.getParameter("amount") != null) {
            userProfileService.topUpBalanceByUserId(user.getId(),
                    BigDecimal.valueOf(Double.parseDouble(req.getParameter("amount"))));
            user.getWallet().setBalance(userProfileService.getWalletBalanceByUserId(user.getId()));
            req.getSession().setAttribute("user", user);
        }
        log.debug("Updating balance was succeed");
        return new ShowUserProfile().execute(req, resp);
    }

}
