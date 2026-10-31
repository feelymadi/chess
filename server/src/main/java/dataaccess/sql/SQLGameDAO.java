package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SQLGameDAO implements GameDAO {
    MySQLHelpers helper = new MySQLHelpers();

    @Override
    public int createGame(GameData game) throws DataAccessException {
        String gameJson = new Gson().toJson(game.game());

        String sqlStatement = """
            INSERT INTO games (whiteUsername, blackUsername, gameName, game)
                VALUES (?,?,?,?);
        """;

        int gameID = helper.executeUpdate(sqlStatement, game.whiteUsername(), game.blackUsername(), game.gameName(), gameJson);
        return gameID;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve game", e);
        }
        return null;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        Integer gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        String gameJson = rs.getString("game");
        ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);

        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = """
                SELECT gameID, whiteUsername, blackUsername, gameName, game 
                FROM games;
            """;
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        games.add(readGame(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve game", e);
        }

        return games;
    }

    @Override
    public void replaceGame(GameData game) throws DataAccessException {
        if (getGame(game.gameID()) == null) {
            throw new DataAccessException("game does not exist");
        }

        String sqlStatement = """
            UPDATE games
                SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ?
                WHERE gameID=?;
        """;

        String gameJson = new Gson().toJson(game.game());
        helper.executeUpdate(sqlStatement, game.whiteUsername(), game.blackUsername(), game.gameName(), gameJson, game.gameID());
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE games";
        helper.executeUpdate(statement);
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
