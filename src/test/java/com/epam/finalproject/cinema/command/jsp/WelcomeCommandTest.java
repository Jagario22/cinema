package com.epam.finalproject.cinema.command.jsp;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.FilmService;
import com.epam.finalproject.cinema.web.command.jsp.WelcomeCommand;
import com.epam.finalproject.cinema.web.constants.path.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class WelcomeCommandTest {

    private WelcomeCommand welcomeCommand;


    private FilmService filmService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;


    @Before
    public void setUp() {
        System.setProperty("logFile", "src\\test\\test_log.log");
        welcomeCommand = new WelcomeCommand();
        filmService = Mockito.mock(FilmService.class);
        welcomeCommand.setFilmService(filmService);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void shouldReturnWelcomePage() throws DBException, IOException {
        String pageName = welcomeCommand.execute(request, response);
        assertEquals(Path.WELCOME_PAGE, pageName);
    }

    @After
    public void afterEach() {
        Mockito.reset(filmService, request, response);
    }
}
