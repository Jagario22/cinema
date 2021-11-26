package com.epam.finalproject.cinema.web.command.jsp;

import com.epam.finalproject.cinema.exception.BadRequestException;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.UserProfileService;
import com.epam.finalproject.cinema.web.model.user.UserProfileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TopUpBalance implements PageCommand {
    private final static Logger log = LogManager.getLogger(TopUpBalance.class);
    private final static UserProfileService USER_PROFILE_SERVICE = UserProfileService.getInstance();
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException, BadRequestException {
        UserProfileInfo user = (UserProfileInfo) req.getSession().getAttribute("user");
        if (user == null) {
            throw new BadRequestException("User wasn't found");
        }

        /*USER_PROFILE_SERVICE.topUpBalanceByWalletId(user.getWallet().getId());*/

        return new ShowUserProfile().execute(req, resp);
    }
}
