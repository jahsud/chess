package service;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.MemoryGameDAO;
import model.GameData;

import java.util.Collection;

public class GameService {
    private final GameDAO gameDAO;

    public GameService (GameDAO gameDAO) throws DataAccessException {
        this.gameDAO = new MemoryGameDAO();
    }

    public void clear () throws DataAccessException {
        MemoryGameDAO.clear();
    }

    public GameData createGame (String gameName) throws DataAccessException {
        return MemoryGameDAO.createGame(gameName);
    }

    public GameData getGame (int gameID) throws DataAccessException {
        return MemoryGameDAO.getGame(gameID);
    }

    public GameData updateGame (int gameID) throws DataAccessException {
        return MemoryGameDAO.updateGame(gameID);
    }

    public Collection<GameData> listGames () throws DataAccessException {
        return MemoryGameDAO.listGames();
    }

}
