package clientTests;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ResponseException;
import ui.ServerFacade;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init () {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void clear () throws ResponseException {
        facade.clear();
    }

    @AfterAll
    static void stopServer () {
        server.stop();
    }

    @Test
    void registerPositiveTest () throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        Assertions.assertNotNull(authData);
    }

    @Test
    void registerNegativeTest () throws Exception {
        facade.register("player1", "password", "email");
        Assertions.assertThrows(ResponseException.class, () -> facade.register("player1", "password", "email"));
    }

    @Test
    public void loginPositiveTest () throws ResponseException {
        facade.register("player1", "password", "email");
        var loginData = facade.login("player1", "password");
        Assertions.assertNotNull(loginData);
    }

    @Test
    public void loginNegativeTest () throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> facade.login("user", "pass"));
    }

    @Test
    public void logoutPositiveTest () throws ResponseException {
        facade.register("player1", "password", "email");
        var loginData = facade.login("player1", "password");
        var logoutData = facade.logout(loginData.authToken());
        Assertions.assertNotNull(logoutData);
    }

    @Test
    public void logoutNegativeTest () throws ResponseException {
        Assertions.assertThrows(ResponseException.class, () -> facade.logout("badToken"));
    }

    @Test
    public void listGamesPositiveTest () throws ResponseException {
        facade.register("player1", "password", "email");
        var loginData = facade.login("player1", "password");
        var listGamesData = facade.listGames(loginData.authToken());
        Assertions.assertNotNull(listGamesData);
    }

    @Test
    public void listGamesNegativeTest () {
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames("badToken"));
    }

    @Test
    public void createGamePositiveTest () throws ResponseException {
        facade.register("player1", "password", "email");
        var loginData = facade.login("player1", "password");
        var createGameData = facade.createGame(loginData.authToken(), "gameName");
        Assertions.assertNotNull(createGameData);
    }

    @Test
    public void createGameNegativeTest () throws ResponseException {
        facade.register("player1", "password", "email");
        var loginData = facade.login("player1", "password");
        Assertions.assertThrows(ResponseException.class, () -> facade.createGame("badToken", "gameName"));
    }

    @Test
    public void joinGamePositiveTest () throws ResponseException {
        facade.register("player1", "password", "email");
        var loginData = facade.login("player1", "password");
        var createGameData = facade.createGame(loginData.authToken(), "gameName");
        var joinGameData = facade.joinGame(loginData.authToken(), "WHITE", createGameData.gameID());
        Assertions.assertNotNull(joinGameData);
    }

    @Test
    public void joinGameNegativeTest () throws ResponseException {
        facade.register("player1", "password", "email");
        var loginData = facade.login("player1", "password");
        var createGameData = facade.createGame(loginData.authToken(), "gameName");
        Assertions.assertThrows(ResponseException.class, () -> facade.joinGame("badToken", "WHITE", createGameData.gameID()));
    }

    @Test
    public void observePositiveTest () throws ResponseException {
        facade.register("player1", "password", "email");
        var loginData = facade.login("player1", "password");
        var createGameData = facade.createGame(loginData.authToken(), "gameName");
        var observeGameData = facade.observe(loginData.authToken(), createGameData.gameID());
        Assertions.assertNotNull(observeGameData);
    }

    @Test
    public void observeNegativeTest () throws ResponseException {
        facade.register("player1", "password", "email");
        var loginData = facade.login("player1", "password");
        var createGameData = facade.createGame(loginData.authToken(), "gameName");
        Assertions.assertThrows(ResponseException.class, () -> facade.observe("badToken", createGameData.gameID()));
    }

}
