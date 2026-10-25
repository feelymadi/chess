package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    }

    // fail - bad request

    // fail - already taken

    // LOGIN

    // pass

    // fail - bad request

    // fail - unauthorized

    // LOGOUT

    // pass

    // fail - unauthorized

    // LIST

    // pass

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
