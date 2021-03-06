package com.epam.finalproject.cinema.web.command.json;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.epam.finalproject.cinema.web.constants.CommandNames.*;
/**
 * Class stores all commands that is used for writing json as response.
 * Provides method for getting command from container
 *
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class CommandContainer {
    private static final Map<String, Command> commands;
    private final static Logger log = LogManager.getLogger(CommandContainer.class);

    static {
        commands = new HashMap<>();
        commands.put(SHOW_FILM_SESSIONS, new ShowAllSessions());
        commands.put(SHOW_AVAILABLE_TICKETS, new ShowAvailableTickets());
    }

    public static Command getCommand(String commandName) {
        if (commandName != null && !commands.containsKey(commandName)) {
            log.debug("no such command, command ->" + commandName);
            throw new IllegalArgumentException("No such command");
        }

        return commands.get(commandName);
    }

    private CommandContainer() {}
}
