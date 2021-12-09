package com.epam.finalproject.cinema.web.controller;

import com.epam.finalproject.cinema.exception.BadRequestException;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.web.command.jsp.PageCommand;
import com.epam.finalproject.cinema.web.command.jsp.PageCommandContainer;
import com.epam.finalproject.cinema.web.constants.path.Path;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

import static com.epam.finalproject.cinema.web.constants.CommandNames.WELCOME_PAGE_COMMAND;
import static com.epam.finalproject.cinema.web.constants.path.Path.*;
import static com.epam.finalproject.cinema.web.constants.SessionAttributes.*;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

@WebServlet(name = "main-page", value = "")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50)   // 50MB
public class Controller extends HttpServlet {
    private final static Logger log = LogManager.getLogger(Controller.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String address = null;
        try {
            address = getAddress(request, response);
        } catch (DBException e) {
            throw new IOException(e.getMessage());
        } catch (IllegalArgumentException e) {
            processError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), request, response);
        }

        request.getRequestDispatcher(address).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String address = getAddress(request, response);
            response.sendRedirect(address);
        } catch (DBException e) {
            throw new IOException(e.getMessage());
        } catch (IllegalArgumentException e) {
            processError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), request, response);
        }
    }

    private String getAddress(HttpServletRequest request, HttpServletResponse response) throws
            IOException, DBException, ServletException {
        String commandName = request.getParameter("command");

        if (commandName == null || commandName.isEmpty()) {
            commandName = WELCOME_PAGE_COMMAND;
        }

        PageCommand pageCommand = PageCommandContainer.getCommand(commandName);
        String address = pageCommand.execute(request, response);
        checkSessionAttributes(request.getSession(), address);
        return address;
    }


    private void checkSessionAttributes(HttpSession session, String address) {
        if (!address.equals(REGISTER_USER_PAGE) && !address.equals(LOGIN_USER_PAGE)) {
            if (session.getAttribute(SUCCESS_REGISTRATION) != null)
                session.removeAttribute(SUCCESS_REGISTRATION);
        }

        if (!address.equals(REGISTER_USER_PAGE)) {
            if (session.getAttribute(UNIQUE_EMAIL_VALIDATION_CLASS) != null) {
                session.removeAttribute(UNIQUE_EMAIL_VALIDATION_CLASS);
            }

            if (session.getAttribute(UNIQUE_LOGIN_VALIDATION_CLASS) != null) {
                session.removeAttribute(UNIQUE_LOGIN_VALIDATION_CLASS);
            }
        }

        if (!address.equals(LOGIN_USER_PAGE)) {
            if (session.getAttribute(SIGN_UP_ERROR) != null) {
                session.removeAttribute(SIGN_UP_ERROR);
            }
        }

        if (!address.equals(USER_PROFILE_PAGE)) {
            if (session.getAttribute(USER_TICKETS) != null)
                session.removeAttribute(USER_TICKETS);

            if (session.getAttribute(TICKET_PURCHASE_ERROR) != null) {
                session.removeAttribute(TICKET_PURCHASE_ERROR);
            }

            if (session.getAttribute(SUCCESS_TICKET_PURCHASE) != null) {
                session.removeAttribute(SUCCESS_TICKET_PURCHASE);
            }
        }
        if (!address.equals(WELCOME_PAGE)) {
            if (session.getAttribute(START_DATE_TIME) != null) {
                session.removeAttribute(START_DATE_TIME);
            }

            if (session.getAttribute(END_DATE_TIME) != null) {
                session.removeAttribute(END_DATE_TIME);
            }
        }
    }


    private void processError(int statusCode, String massage, HttpServletRequest request,
                              HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("javax.servlet.error.status_code",
                statusCode);
        request.setAttribute("javax.servlet.error.message",
                massage);
        request.getRequestDispatcher(Path.ERROR_PAGE).forward(request, response);
    }

    public void destroy() {
    }
}