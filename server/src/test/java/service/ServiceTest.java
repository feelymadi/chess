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
    @Test
    void ListUnauthorized() throws UnauthorizedException, DataAccessException {
        service.LoginResult loginResult = userService.register(new RegisterRequest("username1", "password1", "email1"));
        String auth = loginResult.authToken();

        CreateGameResult game1 = gameService.create(new CreateGameRequest(auth, "game1"));
        CreateGameResult game2 = gameService.create(new CreateGameRequest(auth, "game2"));

        assertThrows(UnauthorizedException.class,
                () -> gameService.list(auth + "1"));
    }

    // CREATE

    // pass
    @Test
    void CreateValid() throws DataAccessException {
        service.LoginResult loginResult = userService.register(new RegisterRequest("username1", "password1", "email1"));
        String auth = loginResult.authToken();

        CreateGameResult game1 = gameService.create(new CreateGameRequest(auth, "game1"));

        ListGamesResult result = gameService.list(auth);

        assertEquals(1, result.games().size());
    }


    // fail - bad request
    @Test
    void CreateBadRequest() throws BadRequestException, DataAccessException {
        service.LoginResult loginResult = userService.register(new RegisterRequest("username1", "password1", "email1"));
        String auth = loginResult.authToken();

        assertThrows(BadRequestException.class,
                () -> gameService.create(new CreateGameRequest(auth, null)));
    }

    // fail - unauthorized
    @Test
    void CreateUnauthorized() throws UnauthorizedException, DataAccessException {
        service.LoginResult loginResult = userService.register(new RegisterRequest("username1", "password1", "email1"));
        String auth = loginResult.authToken();

        assertThrows(UnauthorizedException.class,
                () -> gameService.create(new CreateGameRequest(auth + "1", "game1")));
    }

    // JOIN

    // pass
    @Test
    void JoinValid() throws DataAccessException {
        service.LoginResult loginResult = userService.register(new RegisterRequest("username1", "password1", "email1"));
        String auth = loginResult.authToken();

        CreateGameResult game1 = gameService.create(new CreateGameRequest(auth, "game1"));

        gameService.join(new JoinGameRequest(auth, "WHITE", game1.gameID()));

        GameData game = gameDAO.getGame(game1.gameID());
        assertEquals("username1", game.whiteUsername());
        assertNull(game.blackUsername());
    }

    // fail - bad request
    @Test
    void JoinBadRequest() throws BadRequestException, DataAccessException {
        service.LoginResult loginResult = userService.register(new RegisterRequest("username1", "password1", "email1"));
        String auth = loginResult.authToken();

        CreateGameResult game1 = gameService.create(new CreateGameRequest(auth, "game1"));

        assertThrows(BadRequestException.class,
                () -> gameService.join(new JoinGameRequest(auth, null, game1.gameID())));
    }

    // fail - unauthorized
    @Test
    void JoinUnauthorized() throws UnauthorizedException, DataAccessException {
        service.LoginResult loginResult = userService.register(new RegisterRequest("username1", "password1", "email1"));
        String auth = loginResult.authToken();

        CreateGameResult game1 = gameService.create(new CreateGameRequest(auth, "game1"));

        assertThrows(UnauthorizedException.class,
                () -> gameService.join(new JoinGameRequest(auth + "1", "WHITE", game1.gameID())));
    }

    // fail - already taken
    @Test
    void JoinAlreadyTaken() throws AlreadyTakenException, DataAccessException {
        service.LoginResult loginResult1 = userService.register(new RegisterRequest("username1", "password1", "email1"));
        String auth1 = loginResult1.authToken();
        service.LoginResult loginResult2 = userService.register(new RegisterRequest("username2", "password2", "email2"));
        String auth2 = loginResult2.authToken();

        CreateGameResult game1 = gameService.create(new CreateGameRequest(auth1, "game1"));

        gameService.join(new JoinGameRequest(auth1, "WHITE", game1.gameID()));

        assertThrows(AlreadyTakenException.class,
                () -> gameService.join(new JoinGameRequest(auth2, "WHITE", game1.gameID())));
    }





}
