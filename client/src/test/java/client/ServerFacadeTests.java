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




}
