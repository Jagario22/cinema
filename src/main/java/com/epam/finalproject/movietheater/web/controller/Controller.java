package com.epam.finalproject.movietheater.web.controller;

import com.epam.finalproject.movietheater.web.command.jsp.Command;
import com.epam.finalproject.movietheater.web.command.jsp.CommandContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

import static com.epam.finalproject.movietheater.web.constants.CommandNames.SHOW_MOVIES;
import static com.epam.finalproject.movietheater.web.constants.PagePath.*;
import static com.epam.finalproject.movietheater.web.constants.SessionAttributes.*;

@WebServlet(name = "main-page", value = "")
public class Controller extends HttpServlet {
    private final static Logger log = LogManager.getLogger(Controller.class);


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("doGet#");
        String address = getAddress(request, response);
        checkSessionAttributes(request.getSession(), address);
        request.getRequestDispatcher(address).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String address = getAddress(request, response);
        checkSessionAttributes(request.getSession(), address);
        response.sendRedirect(address);
    }

    private String getAddress(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String commandName = request.getParameter("command");
        if (commandName == null || commandName.isEmpty()) {
            commandName = SHOW_MOVIES;
        }
        Command command = CommandContainer.getCommand(commandName);

        return command.execute(request, response);
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