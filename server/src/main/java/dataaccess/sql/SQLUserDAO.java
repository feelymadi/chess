package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {
    MySQLHelpers helper = new MySQLHelpers();

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String sqlStatement = """
            INSERT INTO users (username, password, email)
                VALUES (?,?,?);
        """;

        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        helper.executeUpdate(sqlStatement, user.username(), hashedPassword, user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Failed to retrieve user", e);        }
        return null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        return new UserData(username, password, email);
    }

    @Override
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE users";
        helper.executeUpdate(statement);
    }

    public void createUsersTable() throws DataAccessException {
        String sqlStatement = """
        CREATE TABLE IF NOT EXISTS users (
            username VARCHAR(256) NOT NULL PRIMARY KEY,
            password VARCHAR(256) NOT NULL,
            email VARCHAR(256) NOT NULL
        )
        """;

        // try to connect to db then try to update using sql statement
        try (var connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement(sqlStatement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to create user table", ex);
        }
    }
}
