package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    private GameService gameService;
    private UserService userService;
    private ClearService clearService;


    @BeforeEach
    void setUp() throws Exception {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
        clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    // CLEAR

    // pass
    @Test
    void clearRemovesAllData() throws DataAccessException {
        userDAO.createUser(new UserData("username", "password", "email"));
        authDAO.createAuth(new AuthData("token", "username"));
        int gameID = gameDAO.createGame(new GameData(0, null, null, "game1", new ChessGame()));

        clearService.clear();

        assertNull(userDAO.getUser("username"));
        assertNull(authDAO.getAuth("token"));
        assertNull(gameDAO.getGame(gameID));
    }

    // REGISTER

    // pass
    @Test
    void RegisterValid() throws DataAccessException {
        userService.register(new RegisterRequest("username1", "password1", "email1"));
        userDAO.getUser("username1");
    }

    // fail - bad request
    @Test
    void RegisterBadRequest() throws BadRequestException, DataAccessException {
        assertThrows(BadRequestException.class,
                () -> userService.register(new RegisterRequest("username1", null, "email1")));
    }

    // fail - already taken
    @Test
    void RegisterAlreadyTaken() throws AlreadyTakenException, DataAccessException {
        userService.register(new RegisterRequest("username1", "password1", "email1"));

        assertThrows(AlreadyTakenException.class,
                () -> userService.register(new RegisterRequest("username1", "password1", "email1")));
    }

    // LOGIN

    // pass
    @Test
    void LoginValid() throws DataAccessException {
        service.LoginResult loginResult = userService.register(new RegisterRequest("username1", "password1", "email1"));
        String token = loginResult.authToken();
        assertNotNull(authDAO.getAuth(token));
    }

    // fail - bad request
    @Test
    void LoginBadRequest() throws BadRequestException, DataAccessException {
        service.LoginResult loginResult = userService.register(new RegisterRequest("username1", "password1", "email1"));
        userService.logout(loginResult.authToken());
        assertThrows(BadRequestException.class,
                () -> userService.login(new LoginRequest("username1", null)));
    }

    // fail - unauthorized
    @Test
    void LoginUnauthorized() throws UnauthorizedException, DataAccessException {
        service.LoginResult loginResult = userService.register(new RegisterRequest("username1", "password1", "email1"));
        userService.logout(loginResult.authToken());
        assertThrows(UnauthorizedException.class,
                () -> userService.login(new LoginRequest("username1", "u")));
    }

    // LOGOUT

    // pass
    @Test
    void LogoutValid() throws DataAccessException {
        service.LoginResult loginResult = userService.register(new RegisterRequest("username1", "password1", "email1"));
        String token = loginResult.authToken();
        assertNotNull(authDAO.getAuth(token));
        userService.logout(token);
        assertNull(authDAO.getAuth(token));
    }

    // fail - unauthorized
    @Test
    void LogoutUnauthorized() throws UnauthorizedException, DataAccessException {
        service.LoginResult loginResult = userService.register(new RegisterRequest("username1", "password1", "email1"));
        assertThrows(UnauthorizedException.class,
                () -> userService.logout(loginResult.authToken() + 1));
    }

    // LIST

    // pass
    @Test
    void ListValid() throws DataAccessException {
        service.LoginResult loginResult = userService.register(new RegisterRequest("username1", "password1", "email1"));
        String auth = loginResult.authToken();

        CreateGameResult game1 = gameService.create(new CreateGameRequest(auth, "game1"));
        CreateGameResult game2 = gameService.create(new CreateGameRequest(auth, "game2"));

        ListGamesResult result = gameService.list(auth);

        assertEquals(2, result.games().size());
    }


    // fail - unauthorized

    // CREATE

    // pass

    // fail - bad request

    // fail - unauthorized

    // JOIN

    // pass

    // fail - bad request

    // fail - unauthorized

    // fail - already taken





}
