package dataAccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static dataAccess.DatabaseManager.configureDatabase;
import static dataAccess.DatabaseManager.executeUpdate;

public class MySqlGameDAO implements GameDAO {

    private final Gson gson = new Gson();

    public MySqlGameDAO () {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS games (
                `gameId` INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                `whiteUsername` VARCHAR(255) NULL,
                `blackUsername` VARCHAR(255) NULL,
                `gameName` VARCHAR(255) NOT NULL,
                `game` LONGTEXT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
        };
        try {
            configureDatabase(createStatements);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear () throws DataAccessException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    @Override
    public GameData createGame (String gameName) throws DataAccessException {
        var statement = "INSERT INTO games (gameName) VALUES (?)";
        int gameID = executeUpdate(statement, gameName);
        return new GameData(gameID, null, null, gameName, null);
    }

    @Override
    public GameData getGame (int gameID) throws DataAccessException {
        var statement = "SELECT * FROM games WHERE gameId = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                var rs = ps.executeQuery();
                if (rs.next()) {
                    return readGame(rs);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to get game: %s, %s", gameID, e.getMessage()));
        }
        return null;
    }

    @Override
    public void updateGame (int gameID, String whiteUsername, String blackUsername) throws DataAccessException {
        var newGame = gson.toJson(getGame(gameID).game());
        var statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?, game = ? WHERE gameId = ?";
        executeUpdate(statement, whiteUsername, blackUsername, newGame, gameID);
    }

    @Override
    public Collection<GameData> listGames () throws DataAccessException {
        var games = new ArrayList<GameData>();
        var statement = "SELECT * FROM games";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                var rs = ps.executeQuery();
                while (rs.next()) {
                    games.add(readGame(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to list games: %s", e.getMessage()));
        }
        return games;
    }

    private GameData readGame (ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameId");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        ChessGame game = gson.fromJson(rs.getString("game"), ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }


}
