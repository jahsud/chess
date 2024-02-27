package dataAccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public interface DataAccess {

    // Game Service
    void clear () throws DataAccessException;

    GameData createGame (String gameName) throws DataAccessException;

    GameData getGame (int gameID) throws DataAccessException;

    GameData updateGame (int gameID) throws DataAccessException;

    Collection<GameData> listGames () throws DataAccessException;

    void deleteGame (int gameID) throws DataAccessException;

    // Auth Service
    AuthData createAuth (String username, String password) throws DataAccessException;

    AuthData getAuth (String authToken) throws DataAccessException;

    void deleteAuth (String authToken) throws DataAccessException;

    // User Service
    UserData createUser (String username, String password, String email) throws DataAccessException;

    UserData getUser (String username) throws DataAccessException;

}
