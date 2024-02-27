package dataAccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class GameDAO implements DataAccess {
    private int nextGameID = 1;
    private final HashMap<Integer, GameData> games = new HashMap<>();

    public void clear () {
        games.clear();
        nextGameID = 1;
    }

    public GameData createGame (String gameName) {
        int gameID = nextGameID++;
        GameData newGame = new GameData(gameID, null, null, gameName, games.get(gameID));
        games.put(gameID, newGame);
        return newGame;
    }

    public GameData getGame (int gameID) {
        return games.get(gameID);
    }

    public GameData updateGame (GameData updatedGame) throws DataAccessException {
        int gameID = updatedGame.getGameID();
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game not found");
        }
        games.put(gameID, updatedGame);
        return updatedGame;
    }

    public Collection<GameData> listGames () {
        return games.values();
    }

    public void deleteGame (int gameID) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new DataAccessException("Game not found");
        }
        games.remove(gameID);
    }
}
