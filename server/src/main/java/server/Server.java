package server;

import com.google.gson.Gson;
import spark.*;

import java.util.Map;

import service.GameService;
import service.UserService;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import dataAccess.MemoryGameDAO;
import model.AuthData;
import model.UserData;
import model.GameData;
import dataAccess.DataAccessException;


public class Server {

    private final UserService userService;
    private final GameService gameService;
    private final Gson gson = new Gson();

    public Server (MemoryUserDAO userDAO, MemoryGameDAO gameDAO, MemoryAuthDAO authDAO) {
        this.userService = new UserService(userDAO, authDAO);
        this.gameService = new GameService(gameDAO);

    }

    public void run (int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/clear", this::clearData);

        Spark.exception(Exception.class, this::handleException);

        Spark.awaitInitialization();
    }

    private Object registerUser (Request req, Response res) {
        UserData userData = gson.fromJson(req.body(), UserData.class);
        try {
            AuthData authData = userService.register(userData);
            res.status(200);
            return gson.toJson(authData);
        } catch (DataAccessException e) {
            res.status(400);
            return gson.toJson(Map.of("message", e.getMessage()));
        }
    }

    private Object loginUser (Request req, Response res) {
        UserData userData = gson.fromJson(req.body(), UserData.class);
        try {
            AuthData authData = userService.login(userData);
            res.status(200);
            return gson.toJson(authData);
        } catch (DataAccessException e) {
            res.status(401); // Unauthorized
            return gson.toJson(Map.of("message", "Invalid credentials"));
        }
    }

    private Object logoutUser (Request req, Response res) {
        String authToken = req.headers("Authorization");
        try {
            userService.logout(new UserData(null, null, null));
            res.status(200);
            return "";
        } catch (DataAccessException e) {
            res.status(401); // Unauthorized
            return gson.toJson(Map.of("message", "Logout failed"));
        }
    }

    private Object listGames (Request req, Response res) {
        String authToken = req.headers("Authorization");
        try {
            var games = gameService.listGames();
            res.status(200);
            res.type("application/json");
            return gson.toJson(Map.of("games", games));
        } catch (DataAccessException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Unauthorized"));
        }
    }

    private Object createGame (Request req, Response res) {
        String authToken = req.headers("Authorization");
        try {
            GameData gameData = gson.fromJson(req.body(), GameData.class);
            var createdGame = gameService.createGame(gameData.gameName());
            res.status(200);
            return gson.toJson(createdGame);
        } catch (DataAccessException e) {
            res.status(400); // Bad Request
            return gson.toJson(Map.of("message", e.getMessage()));
        }
    }

    private Object joinGame (Request req, Response res) {
        String authToken = req.headers("Authorization");
        GameData gameData = gson.fromJson(req.body(), GameData.class);
        try {
            gameService.updateGame(gameData.gameID(), gameData.gameName());
            res.status(200);
            return gson.toJson(Map.of("message", "Game joined successfully"));
        } catch (DataAccessException e) {
            res.status(400); // Bad Request
            return gson.toJson(Map.of("message", "Joining game failed"));
        }
    }

    private Object clearData (Request req, Response res) {
        try {
            // Assuming userService and gameService have clear methods implemented
            userService.clear();
            gameService.clear();
            res.status(200); // OK
            return gson.toJson(Map.of("message", "All data cleared successfully"));
        } catch (DataAccessException e) {
            res.status(500); // Internal Server Error
            return gson.toJson(Map.of("message", "Failed to clear data: " + e.getMessage()));
        }
    }


    private void handleException (Exception ex, Request req, Response res) {
        res.status(500); // Internal Server Error
        res.body(gson.toJson(Map.of("message", "An error occurred: " + ex.getMessage())));
    }

    public void stop () {
        Spark.stop();
        Spark.awaitStop();
    }
}
