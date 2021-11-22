package com.epam.finalproject.movietheater.web.command.json;

import com.epam.finalproject.movietheater.domain.exception.DBException;
import com.epam.finalproject.movietheater.service.TicketService;
import com.epam.finalproject.movietheater.web.command.Command;
import com.epam.finalproject.movietheater.web.model.TicketDTO;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ShowAvailableTickets implements Command {
    private final static TicketService ticketService = TicketService.getInstance();
    private final static Logger log = LogManager.getLogger(ShowAllSessions.class);

    @Override
    public void execute(HttpServletRequest req, HttpServletResponse resp) throws IOException, DBException {
        String sessionId = req.getParameter("sessionId");
        if (sessionId != null) {
            List<TicketDTO> tickets = ticketService.getTicketsBySessionId(Integer.parseInt(sessionId));
            resp.getWriter().write(new Gson().toJson(tickets));
        }

        log.debug("Got tickets for sessionId: " + sessionId);
    }
}