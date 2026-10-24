package dataaccess;

import model.AuthData;
import model.GameData;

public interface GameDAO {

    int createGame(GameData game) throws DataAccessException;

    void clear() throws DataAccessException;
}
