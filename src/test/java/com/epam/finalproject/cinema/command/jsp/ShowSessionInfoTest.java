package com.epam.finalproject.cinema.command.jsp;

import com.epam.finalproject.cinema.domain.user.User;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.SessionService;
import com.epam.finalproject.cinema.service.TicketService;
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
public class ShowSessionInfoTest  {
    private ShowSessionInfo showSessionInfo;

    private SessionService sessionService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;


    @Before
    public void setUp() {
        System.setProperty("logFile", "src\\test\\test_log.log");
        showSessionInfo = new ShowSessionInfo();
        sessionService = Mockito.mock(SessionService.class);
        showSessionInfo.setSessionService(sessionService);
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void shouldReturnLoginUserPageIfNullUser() throws DBException, IOException {
        String pageName = showSessionInfo.execute(request, response);
        assertEquals(Path.LOGIN_USER_PAGE, pageName);
    }

    @Test
    public void shouldReturnLoginUserPage() throws DBException, IOException {
        when(session.getAttribute("user")).thenReturn(Mockito.mock(User.class));
        when(request.getParameter("sessionId")).thenReturn("1");

        String pageName = showSessionInfo.execute(request, response);
        assertEquals(Path.SESSION_INFO_PAGE, pageName);
    }

    @After
    public void afterEach() {
        Mockito.reset(sessionService, request, response);
    }
}
