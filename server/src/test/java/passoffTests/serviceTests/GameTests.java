package passoffTests.serviceTests;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.exceptions.AlreadyTakenException;
import dataAccess.exceptions.BadRequestException;
import dataAccess.exceptions.DataAccessException;
import dataAccess.exceptions.UnauthorizedException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.*;
import result.LoginResult;
import result.RegisterResult;
import service.GameService;

import static org.junit.jupiter.api.Assertions.*;
import static passoffTests.serviceTests.UserTests.userService;

public class GameTests {
    static final GameService gameService = new GameService(new MemoryGameDAO(), new MemoryAuthDAO());

    @BeforeEach
    void clear () throws DataAccessException {
        gameService.clear();
    }

    @Test
    void clearGamesTest () throws DataAccessException {
        gameService.clear();
    }

    @Test
    void listGamesNegativeTest () throws DataAccessException, BadRequestException, UnauthorizedException, AlreadyTakenException {
        RegisterRequest registerRequest = new RegisterRequest("userLister", "password", "email");
        RegisterResult user = userService.register(registerRequest);
        assertNotNull(gameService.listGames(new ListGamesRequest(user.authToken())));
    }

//    @Test
//    void createGamePositiveTest () throws DataAccessException {
//        CreateGameRequest request = new CreateGameRequest("game1");
//        gameService.createGame(request);
//    }

}
