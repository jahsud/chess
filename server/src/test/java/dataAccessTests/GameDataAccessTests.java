package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.MySqlGameDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameDataAccessTests {

    private GameDAO gameDAO;

    @BeforeEach
    void setup () throws DataAccessException {
        gameDAO = new MySqlGameDAO();
        gameDAO.clear();
    }

    @Test
    void positiveTestCreateGame () throws DataAccessException {
        var game = gameDAO.createGame("Test Game");
        assertNotNull(game);
        assertEquals("Test Game", game.gameName());
    }

    @Test
    void negativeTestCreateGame () throws DataAccessException {
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    void positiveTestGetGame () throws DataAccessException {
        var game = gameDAO.createGame("Test Game");
        var gameData = gameDAO.getGame(game.gameID());
        assertNotNull(gameData);
        assertEquals(game, gameData);
    }

    @Test
    void negativeTestGetGame () throws DataAccessException {
        var gameData = gameDAO.getGame(1);
        assertNull(gameData);
    }

    @Test
    void positiveTestUpdateGame () throws DataAccessException {
        var game = gameDAO.createGame("Test Game");
        gameDAO.updateGame(game.gameID(), "white", "black");
        var gameData = gameDAO.getGame(game.gameID());
        assertNotNull(gameData);
        assertEquals("white", gameData.whiteUsername());
        assertEquals("black", gameData.blackUsername());
    }

    @Test
    void negativeTestUpdateGame () {
        assertThrows(NullPointerException.class, () -> gameDAO.updateGame(1, "white", "black"));
    }

    @Test
    void positiveTestListGames () throws DataAccessException {
        var game = gameDAO.createGame("Test Game");
        var games = gameDAO.listGames();
        assertNotNull(games);
        assertEquals(1, games.size());
        assertEquals(game, games.iterator().next());
    }

    @Test
    void negativeTestListGames () throws DataAccessException {
        var games = gameDAO.listGames();
        assertNotNull(games);
        assertEquals(0, games.size());
    }


}
