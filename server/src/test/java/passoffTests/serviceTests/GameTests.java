package passoffTests.serviceTests;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
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
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class GameTests {
    GameService gameService;
    UserService userService;

    @BeforeEach
    void setup () throws DataAccessException {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();

        this.userService = new UserService(userDAO, authDAO);
        this.gameService = new GameService(gameDAO, authDAO);

        userService.clear();
        gameService.clear();
    }

    @Test
    void clearGamesTest () throws DataAccessException {
        gameService.clear();
    }

    @Test
    void listGamesPositiveTest () throws DataAccessException, BadRequestException, UnauthorizedException, AlreadyTakenException {
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "email");
        RegisterResult user = userService.register(registerRequest);
        assertNotNull(gameService.listGames(new ListGamesRequest(user.authToken())));
    }

    @Test
    void listGamesNegativeTest () throws DataAccessException, BadRequestException, UnauthorizedException, AlreadyTakenException {
        ListGamesRequest request = new ListGamesRequest("badToken");
        assertThrows(DataAccessException.class, () -> gameService.listGames(request));
    }

    @Test
    void createGamePositiveTest () throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "email");
        RegisterResult user = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(user.authToken(), "gameName");
        assertNotNull(gameService.createGame(createGameRequest));
    }

    @Test
    void createGameNegativeTest () throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        CreateGameRequest request = new CreateGameRequest("badToken", "gameName");
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(request));
    }

}
