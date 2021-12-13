package com.epam.finalproject.cinema.web.command.jsp;

import com.epam.finalproject.cinema.web.command.jsp.admin.*;
import com.epam.finalproject.cinema.web.command.jsp.user.*;
import com.epam.finalproject.cinema.web.command.jsp.login.LoginCommand;
import com.epam.finalproject.cinema.web.command.jsp.login.LogoutCommand;
import com.epam.finalproject.cinema.web.command.jsp.login.RegisterCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.epam.finalproject.cinema.web.constants.CommandNames.*;
/**
 * Class stores all commands that is used for getting page address to forward or redirect
 * Provides method for getting command from container
 *
 * @author Vlada Volska
 * @version 1.0
 * @since 2021.12.05
 *
 */
public class PageCommandContainer {
    private static final Map<String, PageCommand> commands;
    private final static Logger log = LogManager.getLogger(PageCommandContainer.class);

    static {
        commands = new HashMap<>();
        commands.put(LOGIN, new LoginCommand());
        commands.put(REGISTER, new RegisterCommand());
        commands.put(WELCOME_PAGE_COMMAND, new WelcomeCommand());
        commands.put(SHOW_MOVIE_INFO, new ShowMovieCommand());
        commands.put(SHOW_FiLM_SESSION_INFO, new ShowSessionInfo());
        commands.put(SHOW_PROFILE, new ShowUserProfile());
        commands.put(BUY_TICKET, new BuyTicketCommand());
        commands.put(LOGOUT, new LogoutCommand());
        commands.put(TOP_UP_BALANCE, new TopUpBalanceCommand());
        commands.put(SHOW_ADD_MOVIE_PAGE, new ShowAddMoviePageCommand());
        commands.put(ADD_MOVIE, new AddMovieCommand());
        commands.put(ADMIN_SHOW_ALL_MOVIES_INFO, new ShowAllMoviesInfoCommand());
        commands.put(ADMIN_SHOW_SCHEDULE, new ShowAllSessionsInfoCommand());
        commands.put(ADMIN_ADD_SESSION, new AddSessionCommand());
        commands.put(ADMIN_CANCEL_SESSION, new CancelSessionCommand());
        commands.put(ADMIN_SHOW_ALL_USERS, new ShowUsersCommand());
        commands.put(ADMIN_CHANGE_ROLE, new ChangeUserRoleCommand());
    }

    public static PageCommand getCommand(String commandName) {
        if (commandName != null && !commands.containsKey(commandName)) {
            log.debug("no such command, command ->" + commandName);
            throw new IllegalArgumentException("No such command");
        }

        return commands.get(commandName);
    }

    private PageCommandContainer() {
    }

}
