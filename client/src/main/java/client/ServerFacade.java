package client;
import com.google.gson.Gson;
import model.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    // register
    public LoginResult register(String username, String password, String email) throws ResponseException {
        var request = buildRequest("POST", "/user", new RegisterRequest(username, password, email), null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    // login
    public LoginResult login(String username, String password) throws ResponseException {
        var request = buildRequest("POST", "/session", new LoginRequest(username, password), null);
        var response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    // logout
    public void logout(String authToken) throws ResponseException {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    // create
    public CreateGameResult create(String gameName, String authToken) throws ResponseException {
        var request = buildRequest("POST", "/game", new CreateGameRequest(authToken, gameName), authToken);
        var response = sendRequest(request);
        return handleResponse(response, CreateGameResult.class);
    }

    // list
    public ListGamesResult listGames(String authToken) throws ResponseException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        return handleResponse(response, ListGamesResult.class);
    }

    // play game
    public void playGame(String authToken, String playerColor, Integer gameID) throws ResponseException {
        var request = buildRequest("PUT", "/game", new JoinGameRequest(authToken, playerColor, gameID), authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    // clear all
    public void clearAll() throws ResponseException{
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }


    // HELPER FUNCTIONS
    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }

        if (authToken != null) {
            request.setHeader("authorization", authToken);
        }

        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body, status);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }

}


