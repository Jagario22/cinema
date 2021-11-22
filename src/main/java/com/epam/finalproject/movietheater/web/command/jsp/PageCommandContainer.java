package com.epam.finalproject.movietheater.web.command.jsp;

import com.epam.finalproject.movietheater.web.command.login.LoginCommand;
import com.epam.finalproject.movietheater.web.command.login.RegisterCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.epam.finalproject.movietheater.web.constants.CommandNames.*;

public class PageCommandContainer {
    private static final Map<String, PageCommand> commands;
    private final static Logger log = LogManager.getLogger(PageCommandContainer.class);

    static {
        commands = new HashMap<>();
        commands.put(LOGIN, new LoginCommand());
        commands.put(REGISTER, new RegisterCommand());
        commands.put(WELCOME_PAGE_COMMAND, new WelcomeCommand());
        commands.put(SHOW_MOVIE_INFO, new ShowMovieCommand());
        commands.put(SHOW_SESSION_INFO, new ShowSessionInfo());
    }

    public static PageCommand getCommand(String commandName) {
        log.debug(commandName);
        if (commandName != null && !commands.containsKey(commandName)) {
            log.debug("no such command, command ->" + commandName);
            throw new IllegalArgumentException("No such command");
        }

        return commands.get(commandName);
    }

    private PageCommandContainer() {}

}
