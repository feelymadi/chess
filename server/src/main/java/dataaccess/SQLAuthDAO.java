package dataaccess;

import model.AuthData;

import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{
    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }


    public void createAuthsTable() throws DataAccessException {
        String sqlStatement = """
        CREATE TABLE IF NOT EXISTS auths (
            authToken VARCHAR(256) NOT NULL PRIMARY KEY,
            username VARCHAR(256) NOT NULL
        )
        """;

        // try to connect to db then try to update using sql statement
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(sqlStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to create auths table", ex);
        }
    }
}
