package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {

    void clear() throws DataAccessException;

    GameData createGame(String gameName) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(int gameID, String whiteUsername, String blackUsername, ChessGame game) throws DataAccessException;

    Collection<GameData> listGames() throws DataAccessException;

}
