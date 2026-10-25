package dataaccess;

import model.AuthData;
import model.GameData;
import service.ListGamesResult;

import javax.xml.crypto.Data;
import java.util.Collection;

public interface GameDAO {

    int createGame(GameData game) throws DataAccessException;

    GameData getGame(Integer gameID) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

    void replaceGame(GameData game) throws DataAccessException;

    void clear() throws DataAccessException;
}
