package server;

import io.javalin.*;
import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import service.ClearService;

public class Server {

    private final Javalin javalin;
    private final ClearService clearService;

    public Server() {
        // construct Daos
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        clearService = new ClearService(userDAO, authDAO, gameDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", this::clear);

    }

    private void clear(Context ctx) throws DataAccessException{
        clearService.clear();
        ctx.status(200).result("{}");
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
