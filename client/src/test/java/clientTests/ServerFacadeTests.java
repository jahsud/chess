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

}
