package passoffTests.serviceTests;

import dataAccess.MemoryGameDAO;
import dataAccess.exceptions.AlreadyTakenException;
import dataAccess.exceptions.BadRequestException;
import dataAccess.exceptions.DataAccessException;
import dataAccess.exceptions.UnauthorizedException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.JoinGameRequest;
import request.CreateGameRequest;
import request.ListGamesRequest;
import service.GameService;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {
    static final GameService gameService = new GameService(new MemoryGameDAO());

    @BeforeEach
    void clear () throws DataAccessException {
        gameService.clear();
    }

    @Test
    void clearGamesTest () throws DataAccessException {
        gameService.clear();
    }

//    @Test
//    void createGamePositiveTest () throws DataAccessException {
//        CreateGameRequest request = new CreateGameRequest("game1");
//        gameService.createGame(request);
//    }

}
