package com.epam.finalproject.movietheater.web.command.json;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.epam.finalproject.movietheater.web.constants.CommandNames.*;

public class JsonCommandContainer {
    private static final Map<String, JsonCommand> commands;
    private final static Logger log = LogManager.getLogger(JsonCommandContainer.class);

    static {
        commands = new HashMap<>();
        commands.put(SHOW_SESSIONS, new ShowAllSessions());
    }

    public static JsonCommand getCommand(String commandName) {
        if (commandName != null && !commands.containsKey(commandName)) {
            log.debug("no such command, command ->" + commandName);
            throw new IllegalArgumentException("No such command");
        }

        return commands.get(commandName);
    }

    private JsonCommandContainer() {}
}
