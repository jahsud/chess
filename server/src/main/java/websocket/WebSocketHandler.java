package websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.*;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.userCommands.*;
import webSocketMessages.serverMessages.*;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    MySqlGameDAO gameDAO = new MySqlGameDAO();
    MySqlUserDAO userDAO = new MySqlUserDAO();
    MySqlAuthDAO authDAO = new MySqlAuthDAO();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(new Gson().fromJson(message, JoinPlayer.class), session);
            case JOIN_OBSERVER -> joinObserver(new Gson().fromJson(message, JoinObserver.class), session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMove.class), session);
            case LEAVE -> leave(new Gson().fromJson(message, Leave.class), session);
            case RESIGN -> resign(new Gson().fromJson(message, Resign.class), session);
        }
    }

    private void joinPlayer(JoinPlayer command, Session session) throws IOException {
        try {
            connections.addConnection(command.getAuthString(), command.gameID, session);

            if (command.getAuthString() == null) {
                throw new DataAccessException("Auth token must be specified.");
            } else if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.");
            } else if (command.playerColor == null || (!command.playerColor.equals("WHITE") && !command.playerColor.equals("BLACK"))) {
                throw new DataAccessException("Proper player color must be specified (WHITE/BLACK). Or type \"observe <ID>\" to join as an observer.\n");
            } else if (gameDAO.getGame(command.gameID) == null || gameDAO.getGame(command.gameID).game() == null) {
                throw new DataAccessException("Game has not started yet.\n");
            } else if (authDAO.getAuth(command.getAuthString()) == null || authDAO.getAuth(command.getAuthString()).authToken() == null) {
                throw new DataAccessException("Invalid auth token.");
            } else if (userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()) == null || userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()).username() == null) {
                throw new DataAccessException("User does not exist.");
            } else if (!Objects.equals(gameDAO.getGame(command.gameID).whiteUsername(), authDAO.getAuth(command.getAuthString()).username()) && command.playerColor.equals("WHITE")) {
                throw new DataAccessException("Incorrect player trying to join.");
            } else if (!Objects.equals(gameDAO.getGame(command.gameID).blackUsername(), authDAO.getAuth(command.getAuthString()).username()) && command.playerColor.equals("BLACK")) {
                throw new DataAccessException("Incorrect player trying to join.");
            } else {

                LoadGame loadGame = new LoadGame(gameDAO.getGame(command.gameID).game());
                session.getRemote().sendString(new Gson().toJson(loadGame));

                String message = String.format("Player %s has joined the game for the %s team", authDAO.getAuth(command.getAuthString()).username(), command.playerColor);
                Notification notification = new Notification(message);
                connections.broadcastToOthers(command.getAuthString(), command.gameID, new Gson().toJson(notification));
            }

        } catch (DataAccessException | IOException e) {
            Error error = new Error(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void joinObserver(JoinObserver command, Session session) throws IOException {
        try {
            connections.addConnection(command.getAuthString(), command.gameID, session);

            if (command.getAuthString() == null) {
                throw new DataAccessException("Auth token must be specified.");
            } else if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.");
            } else if (gameDAO.getGame(command.gameID) == null || gameDAO.getGame(command.gameID).game() == null) {
                throw new DataAccessException("Game has not started yet.");
            } else if (authDAO.getAuth(command.getAuthString()) == null || authDAO.getAuth(command.getAuthString()).authToken() == null) {
                throw new DataAccessException("Invalid auth token.");
            } else if (userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()) == null || userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()).username() == null) {
                throw new DataAccessException("User does not exist.");
            } else {

                LoadGame loadGame = new LoadGame(gameDAO.getGame(command.gameID).game());
                session.getRemote().sendString(new Gson().toJson(loadGame));

                String message = String.format("Player %s has joined the game as an observer", authDAO.getAuth(command.getAuthString()).username());
                Notification notification = new Notification(message);
                connections.broadcastToOthers(command.getAuthString(), command.gameID, new Gson().toJson(notification));
            }

        } catch (DataAccessException | IOException e) {
            Error error = new Error(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void makeMove(MakeMove command, Session session) throws IOException {
        try {

            if (command.getAuthString() == null) {
                throw new DataAccessException("Auth token must be specified.");
            } else if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.");
            } else if (command.move == null) {
                throw new DataAccessException("Move must be specified.");
            } else if (gameDAO.getGame(command.gameID) == null || gameDAO.getGame(command.gameID).game() == null) {
                throw new DataAccessException("Game has not started yet.");
            } else if (authDAO.getAuth(command.getAuthString()) == null || authDAO.getAuth(command.getAuthString()).authToken() == null) {
                throw new DataAccessException("Invalid auth token.");
            } else if (userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()) == null || userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()).username() == null) {
                throw new DataAccessException("User does not exist.");
            } else if (gameDAO.getGame(command.gameID).game().getTeamTurn().toString().equals("WHITE") && !gameDAO.getGame(command.gameID).whiteUsername().equals(authDAO.getAuth(command.getAuthString()).username())) {
                throw new DataAccessException("It is not your turn.");
            } else if (gameDAO.getGame(command.gameID).game().getTeamTurn().toString().equals("BLACK") && !gameDAO.getGame(command.gameID).blackUsername().equals(authDAO.getAuth(command.getAuthString()).username())) {
                throw new DataAccessException("It is not your turn.");
            } else if (gameDAO.getGame(command.gameID).game().isGameOver()) {
                throw new DataAccessException("Game is over.");
            } else {

                try {

                    GameData games = gameDAO.getGame(command.gameID);
                    games.game().makeMove(command.move);
                    gameDAO.updateGame(command.gameID, games.whiteUsername(), games.blackUsername(), games.game());

                    if (games.game().isInCheckmate(games.game().getTeamTurn())) {
                        games.game().endGame();
                        gameDAO.updateGame(command.gameID, games.whiteUsername(), games.blackUsername(), games.game());
                    }

                    LoadGame loadGame = new LoadGame(games.game());
                    connections.broadcastToAll(command.gameID, new Gson().toJson(loadGame));

                    String message = String.format("Player %s has made a move", authDAO.getAuth(command.getAuthString()).username());
                    Notification notification = new Notification(message);
                    connections.broadcastToOthers(command.getAuthString(), command.gameID, new Gson().toJson(notification));

                } catch (InvalidMoveException e) {
                    Error error = new Error(e.getMessage());
                    session.getRemote().sendString(new Gson().toJson(error));
                }

            }

        } catch (DataAccessException | IOException e) {
            Error error = new Error(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void leave(Leave command, Session session) throws IOException {
        try {

            if (command.getAuthString() == null) {
                throw new DataAccessException("Auth token must be specified.");
            } else if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.");
            } else if (gameDAO.getGame(command.gameID) == null || gameDAO.getGame(command.gameID).game() == null) {
                throw new DataAccessException("Game has not started yet.");
            } else if (authDAO.getAuth(command.getAuthString()) == null || authDAO.getAuth(command.getAuthString()).authToken() == null) {
                throw new DataAccessException("Invalid auth token.\n");
            } else if (userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()) == null || userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()).username() == null) {
                throw new DataAccessException("User does not exist.\n");
            } else {

                var playerLeaving = authDAO.getAuth(command.getAuthString()).username();
                var whitePlayerInGame = gameDAO.getGame(command.gameID).whiteUsername();
                var blackPlayerInGame = gameDAO.getGame(command.gameID).blackUsername();
                var game = gameDAO.getGame(command.gameID).game();

                if (Objects.equals(playerLeaving, whitePlayerInGame)) {
                    gameDAO.updateGame(command.gameID, null, blackPlayerInGame, game);

                    String message = String.format("Player %s has left the game", authDAO.getAuth(command.getAuthString()).username());
                    Notification notification = new Notification(message);
                    connections.broadcastToOthers(command.getAuthString(), command.gameID, new Gson().toJson(notification));
                    connections.removeConnection(command.getAuthString());

                } else if (Objects.equals(playerLeaving, blackPlayerInGame)) {
                    gameDAO.updateGame(command.gameID, whitePlayerInGame, null, game);

                    String message = String.format("Player %s has left the game", authDAO.getAuth(command.getAuthString()).username());
                    Notification notification = new Notification(message);
                    connections.broadcastToOthers(command.getAuthString(), command.gameID, new Gson().toJson(notification));
                    connections.removeConnection(command.getAuthString());

                } else {
                    String message = String.format("Observer %s has left the game", authDAO.getAuth(command.getAuthString()).username());
                    Notification notification = new Notification(message);
                    connections.broadcastToOthers(command.getAuthString(), command.gameID, new Gson().toJson(notification));
                    connections.removeConnection(command.getAuthString());
                }

            }

        } catch (DataAccessException | IOException e) {
            Error error = new Error(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void resign(Resign command, Session session) throws IOException {
        try {

            if (command.getAuthString() == null) {
                throw new DataAccessException("Auth token must be specified.");
            } else if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.");
            } else if (gameDAO.getGame(command.gameID).game().isGameOver()) {
                throw new DataAccessException("Game is over.\n");
            } else if (gameDAO.getGame(command.gameID) == null || gameDAO.getGame(command.gameID).game() == null) {
                throw new DataAccessException("Game has not started yet.\n");
            } else if (authDAO.getAuth(command.getAuthString()) == null || authDAO.getAuth(command.getAuthString()).authToken() == null) {
                throw new DataAccessException("Invalid auth token.\n");
            } else if (userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()) == null || userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()).username() == null) {
                throw new DataAccessException("User does not exist.\n");
            } else {

                var playerResigning = authDAO.getAuth(command.getAuthString()).username();
                var whitePlayerInGame = gameDAO.getGame(command.gameID).whiteUsername();
                var blackPlayerInGame = gameDAO.getGame(command.gameID).blackUsername();
                var game = gameDAO.getGame(command.gameID).game();

                if (Objects.equals(playerResigning, whitePlayerInGame) || Objects.equals(playerResigning, blackPlayerInGame)) {
                    game.endGame();
                    gameDAO.updateGame(command.gameID, whitePlayerInGame, blackPlayerInGame, game);

                    String message = String.format("Player %s has resigned. Game is over.", playerResigning);
                    Notification notification = new Notification(message);

                    connections.broadcastToAll(command.gameID, new Gson().toJson(notification));
                    connections.removeConnection(command.getAuthString());

                } else {
                    throw new DataAccessException("You are not a player in this game.");
                }

            }

        } catch (DataAccessException | IOException e) {
            Error error = new Error(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }


}
