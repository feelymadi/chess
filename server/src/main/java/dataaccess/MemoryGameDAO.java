package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int nextGameID = 1000;

    @Override
    public int createGame(GameData game) throws DataAccessException {
        nextGameID++;
        int gameID = nextGameID;

        games.put(gameID , new GameData(gameID, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game()));

        return gameID;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    @Override
    public void clear() throws DataAccessException {
        nextGameID = 1000;
        games.clear();

    }
}
