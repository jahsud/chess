package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {

    void clear () throws DataAccessException;

    GameData createGame (String gameName) throws DataAccessException;

    GameData getGame (int gameID) throws DataAccessException;

    GameData updateGame (int gameID, String newGameName) throws DataAccessException;

    Collection<GameData> listGames () throws DataAccessException;

}
