package com.epam.finalproject.cinema.web.command.json;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.TicketService;
import com.epam.finalproject.cinema.web.model.ticket.TicketInfo;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
/**
 * This command is used for writing all available tickets in response as a json
 *
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class ShowAvailableTickets implements Command {
    private final static TicketService ticketService = TicketService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowAllSessions.class);

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        String sessionId = req.getParameter("sessionId");
        if (sessionId != null) {
            List<TicketInfo> tickets = ticketService.getTicketsInfoBySessionIdWhereUserIsNull(Integer.parseInt(sessionId));
            resp.getWriter().write(new Gson().toJson(tickets));
        }

        log.debug("Got tickets for sessionId: " + sessionId);
    }
}