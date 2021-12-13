package com.epam.finalproject.cinema.web.command.jsp.admin;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.UserProfileService;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ChangeUserRoleCommand implements PageCommand {
    private final static UserProfileService userProfileService = UserProfileService.getInstance();
    private final static Logger log = LogManager.getLogger(ChangeUserRoleCommand.class);


    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException, ServletException {
        String role = req.getParameter("role");
        String userId = req.getParameter("id");
        int id = Integer.parseInt(userId);
        userProfileService.updateUserRole(id, role);
        return new ShowUsersCommand().execute(req,resp);
    }
}
