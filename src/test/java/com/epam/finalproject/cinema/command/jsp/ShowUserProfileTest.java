package com.epam.finalproject.cinema.command.jsp;

import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.FilmService;
import com.epam.finalproject.cinema.service.TicketService;
import com.epam.finalproject.cinema.service.UserProfileService;
import com.epam.finalproject.cinema.web.command.jsp.WelcomeCommand;
import com.epam.finalproject.cinema.web.command.jsp.user.ShowUserProfile;
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
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ShowUserProfileTest  {
    private ShowUserProfile showUserProfile;

    private TicketService ticketService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;


    @Before
    public void setUp() {
        System.setProperty("logFile", "src\\test\\test_log.log");
        showUserProfile = new ShowUserProfile();
        ticketService = Mockito.mock(TicketService.class);
        showUserProfile.setTicketService(ticketService);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void shouldReturnUserProfilePage() throws DBException, IOException {
        String pageName = showUserProfile.execute(request, response);
        assertEquals(Path.USER_PROFILE_PAGE, pageName);
    }

    @After
    public void afterEach() {
        Mockito.reset(ticketService, request, response);
    }
}
