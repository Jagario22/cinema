package com.epam.finalproject.movietheater.web.controller;

import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.web.command.jsp.PageCommand;
import com.epam.finalproject.movietheater.web.command.jsp.PageCommandContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

import static com.epam.finalproject.movietheater.web.constants.CommandNames.WELCOME_PAGE_COMMAND;
import static com.epam.finalproject.movietheater.web.constants.PagePath.*;
import static com.epam.finalproject.movietheater.web.constants.SessionAttributes.*;

@WebServlet(name = "main-page", value = "")
public class Controller extends HttpServlet {
    private final static Logger log = LogManager.getLogger(Controller.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("doGet#");
        try {
            String address = getAddress(request, response);
            checkSessionAttributes(request.getSession(), address);
            request.getRequestDispatcher(address).forward(request, response);
        } catch (DBException | IOException e) {
            log.debug(e.getCause().getMessage() + "\n" + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.debug("doPost");
        try {
            String address = getAddress(request, response);
            checkSessionAttributes(request.getSession(), address);
            response.sendRedirect(address);
        } catch (DBException | IOException e) {
            log.debug(e.getCause().getMessage() + "\n" + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private String getAddress(HttpServletRequest request, HttpServletResponse response) throws
            IOException, DBException {
        String commandName = request.getParameter("command");

        if (commandName == null || commandName.isEmpty()) {
            commandName = WELCOME_PAGE_COMMAND;
        }
        PageCommand pageCommand = PageCommandContainer.getCommand(commandName);
        return pageCommand.execute(request, response);
    }


    private void checkSessionAttributes(HttpSession session, String address) {
        if (!address.equals(LOGIN_USER_PAGE)) {
            if (session.getAttribute(SIGN_UP_ERROR) != null) {
                session.removeAttribute(SIGN_UP_ERROR);
            }
        }
        if (!address.equals(REGISTER_USER_PAGE)) {
            if (session.getAttribute(UNIQUE_EMAIL_VALIDATION_CLASS) != null) {
                session.removeAttribute(UNIQUE_EMAIL_VALIDATION_CLASS);
            }

            if (session.getAttribute(UNIQUE_LOGIN_VALIDATION_CLASS) != null) {
                session.removeAttribute(UNIQUE_LOGIN_VALIDATION_CLASS);
            }
        }

        if (!address.equals(REGISTER_USER_PAGE) && !address.equals(LOGIN_USER_PAGE)) {
            session.removeAttribute(SUCCESS_REGISTRATION);
        }
    }

    public void destroy() {
    }
}