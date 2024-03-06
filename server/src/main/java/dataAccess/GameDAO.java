package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {

    void clear () throws DataAccessException;

    GameData createGame (String gameName) throws DataAccessException;

    GameData getGame (int gameID) throws DataAccessException;

    void updateGame (int gameID, String whiteUsername, String blackUsername) throws DataAccessException;

    Collection<GameData> listGames () throws DataAccessException;

}
