package client;

import model.LoginRequest;
import model.RegisterRequest;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void cleanup() throws ResponseException {
        facade.clearAll();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    // register positive
    @Test
    public void registerPositive() throws ResponseException {
        var result = facade.register("user", "pass", "email@email.com");
        Assertions.assertEquals("user", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    // register negative
    @Test
    public void registerNegative() throws ResponseException {
        facade.register("user", "pass", "email@email.com");
        var exception = Assertions.assertThrows(ResponseException.class, () -> {
            facade.register("user", "pass", "email@email.com");
        });
        Assertions.assertEquals(
                ResponseException.Code.AlreadyTaken,
                exception.code()
        );
    }

    // login positive
    @Test
    public void loginPositive() throws ResponseException {
        facade.register("user", "pass", "email@email.com");
        var result = facade.login("user", "pass");
        Assertions.assertEquals("user", result.username());
        Assertions.assertNotNull(result.authToken());
    }

    // login negative
    @Test
    public void loginNegativeUnauthorized() throws ResponseException {
        facade.register("user", "pass", "email@email.com");
        var exception = Assertions.assertThrows(ResponseException.class, () -> {
            facade.login("user", "pass1");
        });
        Assertions.assertEquals(
                ResponseException.Code.Unauthorized,
                exception.code()
        );
    }

    // login negative
    @Test
    public void loginNegativeBadRequest() throws ResponseException {
        var exception = Assertions.assertThrows(ResponseException.class, () -> {
            facade.login("user", "pass");
        });
        Assertions.assertEquals(
                ResponseException.Code.Unauthorized,
                exception.code()
        );
    }

    // logout positive
    @Test
    public void logoutPositive() throws ResponseException {
        var result = facade.register("user", "pass", "email@email.com");
        facade.logout(result.authToken());
        var exception = Assertions.assertThrows(ResponseException.class, () -> {
            facade.logout(result.authToken());
        });
        Assertions.assertEquals(
                ResponseException.Code.Unauthorized,
                exception.code()
        );
    }

    // logout negative
    @Test
    public void logoutNegative() throws ResponseException {
        facade.register("user", "pass", "email@email.com");
        var exception = Assertions.assertThrows(ResponseException.class, () -> {
            facade.logout("probably not an auth token");
        });
        Assertions.assertEquals(
                ResponseException.Code.Unauthorized,
                exception.code()
        );
    }

    // create positive
    @Test
    public void createPositive() throws ResponseException {
        var registerResult = facade.register("user", "pass", "email@email.com");
        String auth = registerResult.authToken();

        var createResult = facade.create("my new game", auth);

        Assertions.assertNotNull(createResult);
        Assertions.assertTrue(createResult.gameID() > 0);

    }

    // create negative
    @Test
    public void createNegative() throws ResponseException {
        facade.register("user", "pass", "email@email.com");
        var exception = Assertions.assertThrows(ResponseException.class, () -> {
            facade.create("my new game", "probably not an auth token");
        });
        Assertions.assertEquals(
                ResponseException.Code.Unauthorized,
                exception.code()
        );
    }

    // list positive
    @Test
    public void listPositive() throws ResponseException {
        var registerResult = facade.register("user", "pass", "email@email.com");
        String auth = registerResult.authToken();
        facade.create("my new game", auth);
        facade.create("other new game", auth);

        var result = facade.listGames(auth);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.games().toArray().length);
    }

    // list negative
    @Test
    public void listNegative() throws ResponseException {
        var registerResult = facade.register("user", "pass", "email@email.com");
        String auth = registerResult.authToken();
        facade.create("my new game", auth);
        facade.create("other new game", auth);

        var exception = Assertions.assertThrows(ResponseException.class, () -> {
            facade.listGames(auth+1);
        });
        Assertions.assertEquals(
                ResponseException.Code.Unauthorized,
                exception.code()
        );

    }

}
