package com.epam.finalproject.cinema.command.jsp.login;

import com.epam.finalproject.cinema.domain.user.User;
import com.epam.finalproject.cinema.exception.DBException;
import com.epam.finalproject.cinema.service.UserProfileService;
import com.epam.finalproject.cinema.web.command.jsp.login.RegisterCommand;
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
import java.util.*;

import static com.epam.finalproject.cinema.web.constants.SessionAttributes.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class RegisterCommandTest {
    private RegisterCommand registerCommand;

    private UserProfileService userProfileService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    private Map<String, String> sessionAttributes;

    @Before
    public void setUp() {
        System.setProperty("logFile", "src\\test\\test_log.log");
        registerCommand = new RegisterCommand();
        userProfileService = Mockito.mock(UserProfileService.class);
        registerCommand.setUserProfileService(userProfileService);
        when(request.getSession()).thenReturn(session);
        sessionAttributes = new HashMap<>();
        doAnswer(invocationOnMock -> {
            sessionAttributes.put(invocationOnMock.getArgument(0), invocationOnMock.getArgument(1));
            return null;
        }).when(session).setAttribute(anyString(), any(Object.class));
    }

    @Test
    public void shouldReturnLoginPage() throws DBException, IOException {
        User user = getValidUser();
        mockUserParams(user.getLogin(), user.getEmail(), user.getPassword());
        when(userProfileService.getUsersWithEqualLoginOrEmail(anyString(), anyString()))
                .thenReturn(new ArrayList<>());

        String page = registerCommand.execute(request, response);
        assertEquals("", sessionAttributes.get(UNIQUE_EMAIL_VALIDATION_CLASS));
        assertEquals("", sessionAttributes.get(UNIQUE_LOGIN_VALIDATION_CLASS));
        assertEquals(true, sessionAttributes.get(SUCCESS_REGISTRATION));
        assertEquals(Path.LOGIN_USER_PAGE, page);
    }

    @Test
    public void shouldSetFalseValidationAttributeAndReturnRegisterPage() throws DBException, IOException {
        User user = getNotValidUser();
        mockUserParams(user.getLogin(), user.getEmail(), user.getPassword());

        String page = registerCommand.execute(request, response);
        assertEquals(null, sessionAttributes.get(UNIQUE_EMAIL_VALIDATION_CLASS));
        assertEquals(null, sessionAttributes.get(UNIQUE_LOGIN_VALIDATION_CLASS));
        assertEquals(null, sessionAttributes.get(SUCCESS_REGISTRATION));
        assertEquals(Path.REGISTER_USER_PAGE, page);
    }

    @Test
    public void shouldSetNotValidUniqueLoginAndEmailAttributesReturnRegisterPage() throws DBException, IOException {
        User user = getValidUser();
        mockUserParams(user.getLogin(), user.getEmail(), user.getPassword());
        when(userProfileService.getUsersWithEqualLoginOrEmail(any(String.class), any(String.class)))
                .thenReturn(getUsersWithEqualLoginAndEmail(user.getLogin(), user.getEmail()));

        String page = registerCommand.execute(request, response);
        assertEquals("is-invalid", sessionAttributes.get(UNIQUE_EMAIL_VALIDATION_CLASS));
        assertEquals("is-invalid", sessionAttributes.get(UNIQUE_LOGIN_VALIDATION_CLASS));
        assertEquals(null, sessionAttributes.get(SUCCESS_REGISTRATION));
        assertEquals(Path.REGISTER_USER_PAGE, page);
    }

    private void mockUserParams(String login, String email, String password) {
        when(request.getParameter("login")).thenReturn(login);
        when(request.getParameter("password")).thenReturn(password);
        when(request.getParameter("email")).thenReturn(email);
    }

    private List<User> getUsersWithEqualLoginAndEmail(String login, String email) {
        User user = new User();
        user.setLogin(login);
        user.setEmail(email);
        return Collections.singletonList(user);
    }

    private List<User> getUsersWithEqualLogin(String login) {
        User user = new User();
        user.setLogin(login);
        return Collections.singletonList(user);

    }

    private List<User> getUsersWithEqualEmail(String email) {
        User user = new User();
        user.setEmail(email);
        return Collections.singletonList(user);

    }

    private User getValidUser() {
        String validLogin = "user1";
        String validPassword = "Password1&";
        String validEmail = "user@gmail.com";
        User validUser = new User();
        validUser.setEmail(validEmail);
        validUser.setLogin(validLogin);
        validUser.setPassword(validPassword);
        return validUser;
    }

    private User getNotValidUser() {
        User notValidUser = new User();
        notValidUser.setLogin("notValidLogin");
        notValidUser.setPassword("notValidPassword");
        notValidUser.setEmail("notValidEmail");

        return notValidUser;
    }

    @After
    public void afterEach() {
        Mockito.reset(userProfileService, request, response, session);
    }

}
