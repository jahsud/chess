package server;

import com.google.gson.Gson;
import dataAccess.*;
import dataAccess.exceptions.*;
import request.*;
import result.*;
import spark.*;

import java.util.Map;

import service.GameService;
import service.UserService;
import model.UserData;
import model.GameData;

public class Server {

    private final UserService userService;
    private final GameService gameService;
    private final Gson gson = new Gson();

    public Server () {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();

        this.userService = new UserService(userDAO, authDAO);
        this.gameService = new GameService(gameDAO, authDAO);
    }

    public int run (int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);

        Spark.exception(Exception.class, this::handleException);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object register (Request req, Response res) {
        RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);
        try {
            RegisterResult authData = userService.register(registerRequest);
            res.status(200);
            return gson.toJson(authData);
        } catch (BadRequestException e) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (AlreadyTakenException e) {
            res.status(403);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    private Object login (Request req, Response res) {
        LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);
        try {
            LoginResult authData = userService.login(loginRequest);
            res.status(200);
            return gson.toJson(authData);
        } catch (UnauthorizedException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    private Object logout (Request req, Response res) {
        String authToken = req.headers("Authorization");
        try {
            userService.logout(new LogoutRequest(authToken));
            res.status(200);
            return "";
        } catch (BadRequestException e) {
            res.status(400); // Unauthorized
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (UnauthorizedException e) {
            res.status(401); // Unauthorized
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500); // Unauthorized
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    private Object listGames (Request req, Response res) {
        String authToken = req.headers("Authorization");
        try {
            var games = gameService.listGames(new ListGamesRequest(authToken));
            res.status(200);
            res.type("application/json");
            return gson.toJson(Map.of("games", games));
        } catch (UnauthorizedException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    private Object createGame (Request req, Response res) {
        String authToken = req.headers("Authorization");
        try {
            GameData gameData = gson.fromJson(req.body(), GameData.class);
            var createdGame = gameService.createGame(new CreateGameRequest(authToken, gameData.gameName()));
            res.status(200);
            return gson.toJson(createdGame);
        } catch (BadRequestException e) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (UnauthorizedException e) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    private Object joinGame (Request req, Response res) {
        String authToken = req.headers("Authorization");
        GameData gameData = gson.fromJson(req.body(), GameData.class);
        try {
            String playerColor = gameData.whiteUsername() != null ? "WHITE" : "BLACK";
            gameService.joinGame(new JoinGameRequest(authToken, playerColor, gameData.gameID()));
            res.status(200);
            return "";
        } catch (BadRequestException e) {
            res.status(400); // Bad Request
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (UnauthorizedException e) {
            res.status(401); // Bad Request
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (AlreadyTakenException e) {
            res.status(403); // Bad Request
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500); // Bad Request
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    private Object clear (Request req, Response res) {
        try {
            userService.clear();
            gameService.clear();
            res.status(200);
            return "";
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
