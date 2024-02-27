package service;

import dataAccess.DataAccessException;
import dataAccess.MemoryGameDAO;
import model.GameData;

import java.util.Collection;

public class GameService {
    private final MemoryGameDAO gameDAO;

    public GameService (MemoryGameDAO gameDAO) throws DataAccessException {
        this.gameDAO = gameDAO;
    }

    public void clear () throws DataAccessException {
        gameDAO.clear();
    }

    public GameData createGame (String gameName) throws DataAccessException {
        return gameDAO.createGame(gameName);
    }

    public GameData getGame (int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }

    public GameData updateGame (int gameID, String gameName) throws DataAccessException {
        return gameDAO.updateGame(gameID, gameName);
    }

    public Collection<GameData> listGames () throws DataAccessException {
        return gameDAO.listGames();
    }

}
