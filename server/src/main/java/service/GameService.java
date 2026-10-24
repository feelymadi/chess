package service;
import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

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

    // join


}
