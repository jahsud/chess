package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private int nextGameID = 1;
    private final HashMap<Integer, GameData> games = new HashMap<>();

    public void clear() {
        games.clear();
        nextGameID = 1;
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public GameData createGame(String gameName) {
        int gameID = nextGameID++;
        GameData newGame = new GameData(gameID, null, null, gameName, null);
        games.put(gameID, newGame);
        return newGame;
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public void updateGame(int gameID, String whiteUsername, String blackUsername, ChessGame game) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game not found");
        }
        GameData gameData = games.get(gameID);
        GameData updatedGameData = new GameData(gameData.gameID(), whiteUsername, blackUsername, gameData.gameName(), gameData.game());
        games.put(gameID, updatedGameData);
    }

}
