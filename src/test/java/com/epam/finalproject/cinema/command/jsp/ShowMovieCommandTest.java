package com.epam.finalproject.cinema.command.jsp;
import com.epam.finalproject.cinema.domain.user.User;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.FilmService;
import com.epam.finalproject.cinema.service.SessionService;
import com.epam.finalproject.cinema.web.command.jsp.user.ShowMovieCommand;
import com.epam.finalproject.cinema.web.command.jsp.user.ShowSessionInfo;
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
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ShowMovieCommandTest  {
    private ShowMovieCommand showMovieCommand;

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
        showMovieCommand = new ShowMovieCommand();
        filmService = Mockito.mock(FilmService.class);
        showMovieCommand.setFilmService(filmService);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void shouldReturnWelcomePageIfNullFilmId() throws DBException, IOException {
        String pageName = showMovieCommand.execute(request, response);
        assertEquals(Path.WELCOME_PAGE, pageName);
    }

    @Test
    public void shouldReturnMovieInfoPage() throws DBException, IOException {
        when(request.getParameter("id")).thenReturn("1");

        String pageName = showMovieCommand.execute(request, response);
        assertEquals(Path.MOVIE_INFO_PAGE, pageName);
    }

    @After
    public void afterEach() {
        Mockito.reset(filmService, request, response);
    }
}
