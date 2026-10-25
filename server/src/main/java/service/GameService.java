package service;
import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameService {

    // initializes
    public final AuthDAO authDAO;
    public final GameDAO gameDAO;

    // constructs
    public GameService(AuthDAO authDAO, GameDAO gameDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    // create game
    public CreateGameResult create(CreateGameRequest createGameRequest) throws BadRequestException, UnauthorizedException, DataAccessException {
        if (authDAO.getAuth(createGameRequest.authToken()) == null) {
            throw new UnauthorizedException("unauthorized");
        }

        if (createGameRequest.authToken() == null || createGameRequest.gameName() == null) {
            throw new BadRequestException("empty request");
        }

        GameData newGame = new GameData(0, null, null, createGameRequest.gameName(), new ChessGame());
        int gameID = gameDAO.createGame(newGame);

        return new CreateGameResult(gameID);
    }

    // list
    public ListGamesResult list(String authToken) throws UnauthorizedException, DataAccessException {
        if (authDAO.getAuth(authToken) == null) {
            throw new UnauthorizedException("unauthorized");
        }

        Collection<GameData> games = gameDAO.listGames();
        List<GameInfo> gameList = new ArrayList<>();

        for (GameData game: games) {
            gameList.add(new GameInfo(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }

        return new ListGamesResult(gameList);
    }

    // join
    public void join(JoinGameRequest joinGameRequest) throws BadRequestException, UnauthorizedException, AlreadyTakenException, DataAccessException {
        if (authDAO.getAuth(joinGameRequest.authToken()) == null) {
            throw new UnauthorizedException("unauthorized");
        }

        String user = authDAO.getAuth(joinGameRequest.authToken()).username();
        String playerColor = joinGameRequest.playerColor();
        GameData game = gameDAO.getGame(joinGameRequest.gameID());
        GameData updatedGame;

        if (joinGameRequest.gameID() == null || gameDAO.getGame(joinGameRequest.gameID()) == null) {
            throw new BadRequestException("bad request");
        }

        if (!"WHITE".equals(playerColor) && !"BLACK".equals(playerColor)) {
            throw new BadRequestException("bad request");
        }

        if ("WHITE".equals(playerColor) && game.whiteUsername() != null){
            throw new AlreadyTakenException("white already taken");
        } else if ("BLACK".equals(playerColor) && game.blackUsername() != null){
            throw new AlreadyTakenException("black already taken");
        }

        if ("WHITE".equals(playerColor)) {
            updatedGame = new GameData(joinGameRequest.gameID(), user, game.blackUsername(), game.gameName(), game.game());
        } else {
            updatedGame = new GameData(joinGameRequest.gameID(), game.whiteUsername(), user, game.gameName(), game.game());
        }

        gameDAO.replaceGame(updatedGame);
    }
}
