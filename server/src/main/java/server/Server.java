package server;

import com.google.gson.Gson;
import io.javalin.*;
import dataaccess.*;
import io.javalin.Javalin;
import io.javalin.http.Context;
import service.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final ClearService clearService;
    private final UserService userService;

    public Server() {
        // construct DAOs
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        javalin = Javalin.create(config -> config.staticFiles.add("web"));


        // initialize services
        clearService = new ClearService(userDAO, authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);

        // routes
        javalin.post("/user", this::register);
        javalin.delete("/db", this::clear);

        // exception handlers
        javalin.exception(BadRequestException.class, (e, ctx) ->
                ctx.status(400).result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage()))));
        javalin.exception(AlreadyTakenException.class, (e, ctx) ->
                ctx.status(403).result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage()))));
        javalin.exception(DataAccessException.class, (e, ctx) ->
                ctx.status(500).result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage()))));
    }

    private void register(Context ctx) throws BadRequestException, AlreadyTakenException, DataAccessException {
        RegisterRequest request = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        if (request == null) {
            throw new BadRequestException("bad request");
        }
        LoginResult result = userService.register(request);
        ctx.status(200).result(new Gson().toJson(result));
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
