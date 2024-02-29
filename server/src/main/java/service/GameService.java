package service;

import dataAccess.exceptions.DataAccessException;
import dataAccess.MemoryGameDAO;
import request.ListGamesRequest;
import result.CreateGameResult;

public class GameService {
    private final MemoryGameDAO gameDAO;

    public GameService (MemoryGameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public void clear () throws DataAccessException {
        gameDAO.clear();
    }

    public ListGamesRequest listGames (String listGamesRequest) throws DataAccessException {

    }

    public CreateGameResult createGame (String gameName) throws DataAccessException {
    }

    public void joinGame (int gameID, String gameName) throws DataAccessException {

    }


}
