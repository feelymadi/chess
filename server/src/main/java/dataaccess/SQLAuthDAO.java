package dataaccess;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{
    MySQLHelpers helper = new MySQLHelpers();

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        String sqlStatement = """
            INSERT INTO auths (authToken, username)
                VALUES (?,?);
        """;

        helper.executeUpdate(sqlStatement, auth.authToken(), auth.username());
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auths WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readAuth(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Failed to retrieve auth", e);        }
        return null;
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        String authToken = rs.getString("authToken");
        String username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE auths";
        helper.executeUpdate(statement);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        var statement = "DELETE FROM auths WHERE authToken=?";
        helper.executeUpdate(statement, authToken);
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
