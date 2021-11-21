package com.epam.finalproject.movietheater.web.command.jsp;

import com.epam.finalproject.movietheater.web.command.jsp.login.LoginCommand;
import com.epam.finalproject.movietheater.web.command.jsp.login.RegisterCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.epam.finalproject.movietheater.web.constants.CommandNames.*;

public class CommandContainer {
    private static final Map<String, Command> commands;
    private final static Logger log = LogManager.getLogger(CommandContainer.class);

    static {
        commands = new HashMap<>();
        commands.put(LOGIN, new LoginCommand());
        commands.put(REGISTER, new RegisterCommand());
        commands.put(SHOW_MOVIES, new ShowMoviesCommand());
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
