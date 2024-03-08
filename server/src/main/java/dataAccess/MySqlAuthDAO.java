package dataAccess;

import model.AuthData;

import java.sql.*;
import java.util.UUID;

import static dataAccess.DatabaseManager.configureDatabase;
import static dataAccess.DatabaseManager.executeUpdate;

public class MySqlAuthDAO implements AuthDAO {

    public MySqlAuthDAO () {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS auth (
                `username` VARCHAR(255) NOT NULL,
                `authToken` VARCHAR(255) NOT NULL,
                PRIMARY KEY(`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
        try {
            configureDatabase(createStatements);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear () throws DataAccessException {
        var statement = "TRUNCATE auth";
        executeUpdate(statement);
    }

    @Override
    public AuthData createAuth (String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        var statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
        executeUpdate(statement, username, authToken);
        return new AuthData(authToken, username);
    }

    @Override
    public AuthData getAuth (String authToken) throws DataAccessException {
        var statement = "SELECT * FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                var rs = ps.executeQuery();
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to get auth: %s, %s", authToken, e.getMessage()));
        }
        return null;
    }

    @Override
    public void deleteAuth (String authToken) throws DataAccessException {
        var statement = "DELETE FROM auth WHERE authToken = ?";
        executeUpdate(statement, authToken);
    }


}
