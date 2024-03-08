package dataAccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import request.RegisterRequest;
import result.CreateGameResult;
import result.RegisterResult;
import service.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameDataAccessTests {
    GameService gameService;
    UserService userService;

    @BeforeEach
    void setup () throws DataAccessException {
        MySqlUserDAO userDAO = new MySqlUserDAO();
        MySqlGameDAO gameDAO = new MySqlGameDAO();
        MySqlAuthDAO authDAO = new MySqlAuthDAO();

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
    void listGamesNegativeTest () {
        ListGamesRequest request = new ListGamesRequest("badToken");
        assertThrows(UnauthorizedException.class, () -> gameService.listGames(request));
    }

    @Test
    void createGamePositiveTest () throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "email");
        RegisterResult user = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(user.authToken(), "nameOfGame");
        assertNotNull(gameService.createGame(createGameRequest));
    }

    @Test
    void createGameNegativeTest () throws DataAccessException, BadRequestException, AlreadyTakenException {
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "email");
        userService.register(registerRequest);
        CreateGameRequest request = new CreateGameRequest("badToken", "gameName");
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(request));
    }

    @Test
    void joinGamePositiveTest () throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "email");
        RegisterResult user = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(user.authToken(), "gameName");
        CreateGameResult game = gameService.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest(user.authToken(), "WHITE", game.gameID());
        gameService.joinGame(joinGameRequest);
    }

    @Test
    void joinGameNegativeTest () throws DataAccessException, UnauthorizedException, BadRequestException, AlreadyTakenException {
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "email");
        RegisterResult user = userService.register(registerRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(user.authToken(), "gameName");
        CreateGameResult game = gameService.createGame(createGameRequest);
        JoinGameRequest joinGameRequest = new JoinGameRequest("badToken", "WHITE", game.gameID());
        assertThrows(UnauthorizedException.class, () -> gameService.joinGame(joinGameRequest));
    }

}
