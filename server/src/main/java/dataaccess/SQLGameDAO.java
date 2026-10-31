package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO{
    @Override
    public int createGame(GameData game) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void replaceGame(GameData game) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }

    public void createGamesTable() throws DataAccessException {
        String sqlStatement = """
        CREATE TABLE IF NOT EXISTS games (
            gameID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
            whiteUsername VARCHAR(255),
            blackUsername VARCHAR(255), 
            gameName VARCHAR(255) NOT NULL,
            game TEXT NOT NULL
        )
        """;

        // try to connect to db then try to update using sql statement
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(sqlStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to create games table", ex);
        }
    }
}
