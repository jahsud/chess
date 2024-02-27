package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.GameData;

import java.util.Collection;

public class GameService {
    private final DataAccess dataAccess;

    public GameService (DataAccess dataAccess) throws DataAccessException {
        this.dataAccess = dataAccess;
    }

    public void clear () throws DataAccessException {
        dataAccess.clear();
    }

    public GameData createGame (String gameName) throws DataAccessException {
        return dataAccess.createGame(gameName);
    }

    public GameData getGame (int gameID) throws DataAccessException {
        return dataAccess.getGame(gameID);
    }

    public GameData updateGame (int gameID) throws DataAccessException {
        return dataAccess.updateGame(gameID);
    }

    public Collection<GameData> listGames () throws DataAccessException {
        return dataAccess.listGames();
    }

    public void deleteGame (int gameID) throws DataAccessException {
        dataAccess.deleteGame(gameID);
    }

}
