package websocket;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.*;
import model.GameData;
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
                throw new DataAccessException("Auth token must be specified.\n");
            } else if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.\n");
            } else if (command.playerColor == null || (!command.playerColor.equals("WHITE") && !command.playerColor.equals("BLACK"))) {
                throw new DataAccessException("Proper player color must be specified (WHITE/BLACK). Or type \"observe <ID>\" to join as an observer.\n");
            } else if (gameDAO.getGame(command.gameID) == null || gameDAO.getGame(command.gameID).game() == null) {
                throw new DataAccessException("Game has not started yet.\n");
            } else if (authDAO.getAuth(command.getAuthString()) == null || authDAO.getAuth(command.getAuthString()).authToken() == null) {
                throw new DataAccessException("Invalid auth token.\n");
            } else if (userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()) == null || userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()).username() == null) {
                throw new DataAccessException("User does not exist.\n");
            } else if (!Objects.equals(gameDAO.getGame(command.gameID).whiteUsername(), authDAO.getAuth(command.getAuthString()).username()) && command.playerColor.equals("WHITE")) {
                throw new DataAccessException("Incorrect player trying to join.\n");
            } else if (!Objects.equals(gameDAO.getGame(command.gameID).blackUsername(), authDAO.getAuth(command.getAuthString()).username()) && command.playerColor.equals("BLACK")) {
                throw new DataAccessException("Incorrect player trying to join.\n");
            } else {

                LoadGame loadGame = new LoadGame(gameDAO.getGame(command.gameID).game());
                session.getRemote().sendString(new Gson().toJson(loadGame));

                String message = String.format("Player %s has joined the game for the %s team", authDAO.getAuth(command.getAuthString()).username(), command.playerColor);
                Notification notification = new Notification(message);
                connections.broadcast(command.getAuthString(), command.gameID, notification);
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
                throw new DataAccessException("Auth token must be specified.\n");
            } else if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.\n");
            } else if (gameDAO.getGame(command.gameID) == null || gameDAO.getGame(command.gameID).game() == null) {
                throw new DataAccessException("Game has not started yet.\n");
            } else if (authDAO.getAuth(command.getAuthString()) == null || authDAO.getAuth(command.getAuthString()).authToken() == null) {
                throw new DataAccessException("Invalid auth token.\n");
            } else if (userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()) == null || userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()).username() == null) {
                throw new DataAccessException("User does not exist.\n");
            } else {

                LoadGame loadGame = new LoadGame(gameDAO.getGame(command.gameID).game());
                session.getRemote().sendString(new Gson().toJson(loadGame));

                String message = String.format("Player %s has joined the game as an observer", authDAO.getAuth(command.getAuthString()).username());
                Notification notification = new Notification(message);
                connections.broadcast(command.getAuthString(), command.gameID, notification);
            }

        } catch (DataAccessException | IOException e) {
            Error error = new Error(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void makeMove(MakeMove command, Session session) throws IOException {
        try {

            if (command.getAuthString() == null) {
                throw new DataAccessException("Auth token must be specified.\n");
            } else if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.\n");
            } else if (command.move == null) {
                throw new DataAccessException("Move must be specified.\n");
            } else if (gameDAO.getGame(command.gameID) == null || gameDAO.getGame(command.gameID).game() == null) {
                throw new DataAccessException("Game has not started yet.\n");
            } else if (authDAO.getAuth(command.getAuthString()) == null || authDAO.getAuth(command.getAuthString()).authToken() == null) {
                throw new DataAccessException("Invalid auth token.\n");
            } else if (userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()) == null || userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()).username() == null) {
                throw new DataAccessException("User does not exist.\n");
            } else if (gameDAO.getGame(command.gameID).game().getTeamTurn().toString().equals("WHITE") && !gameDAO.getGame(command.gameID).whiteUsername().equals(authDAO.getAuth(command.getAuthString()).username())) {
                throw new DataAccessException("It is not your turn.\n");
            } else if (gameDAO.getGame(command.gameID).game().getTeamTurn().toString().equals("BLACK") && !gameDAO.getGame(command.gameID).blackUsername().equals(authDAO.getAuth(command.getAuthString()).username())) {
                throw new DataAccessException("It is not your turn.\n");
            } else {

                try {

                    GameData games = gameDAO.getGame(command.gameID);
                    games.game().makeMove(command.move);
                    gameDAO.updateGame(command.gameID, gameDAO.getGame(command.gameID).whiteUsername(), gameDAO.getGame(command.gameID).blackUsername(), gameDAO.getGame(command.gameID).game());

                    LoadGame loadGame = new LoadGame(games.game());
                    session.getRemote().sendString(new Gson().toJson(loadGame));

                    String message = String.format("Player %s has made a move", authDAO.getAuth(command.getAuthString()).username());
                    Notification notification = new Notification(message);
                    connections.broadcast(command.getAuthString(), command.gameID, notification);

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
                throw new DataAccessException("Auth token must be specified.\n");
            } else if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.\n");
            } else if (gameDAO.getGame(command.gameID) == null || gameDAO.getGame(command.gameID).game() == null) {
                throw new DataAccessException("Game has not started yet.\n");
            } else if (authDAO.getAuth(command.getAuthString()) == null || authDAO.getAuth(command.getAuthString()).authToken() == null) {
                throw new DataAccessException("Invalid auth token.\n");
            } else if (userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()) == null || userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()).username() == null) {
                throw new DataAccessException("User does not exist.\n");
            } else {

                if (Objects.equals(gameDAO.getGame(command.gameID).whiteUsername(), authDAO.getAuth(command.getAuthString()).username())) {
                    gameDAO.updateGame(command.gameID, null, gameDAO.getGame(command.gameID).blackUsername(), gameDAO.getGame(command.gameID).game());
                } else if (Objects.equals(gameDAO.getGame(command.gameID).blackUsername(), authDAO.getAuth(command.getAuthString()).username())) {
                    gameDAO.updateGame(command.gameID, gameDAO.getGame(command.gameID).whiteUsername(), null, gameDAO.getGame(command.gameID).game());
                } else {
                    throw new DataAccessException("You are not a player in this game.\n");
                }

                String message = String.format("Player %s has left the game", authDAO.getAuth(command.getAuthString()).username());
                connections.removeConnection(command.getAuthString());

                Notification notification = new Notification(message);
                connections.broadcast(command.getAuthString(), command.gameID, notification);
            }

        } catch (DataAccessException | IOException e) {
            Error error = new Error(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void resign(Resign command, Session session) throws IOException {
        try {

            if (command.getAuthString() == null) {
                throw new DataAccessException("Auth token must be specified.\n");
            } else if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.\n");
            } else if (gameDAO.getGame(command.gameID) == null || gameDAO.getGame(command.gameID).game() == null) {
                throw new DataAccessException("Game has not started yet.\n");
            } else if (authDAO.getAuth(command.getAuthString()) == null || authDAO.getAuth(command.getAuthString()).authToken() == null) {
                throw new DataAccessException("Invalid auth token.\n");
            } else if (userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()) == null || userDAO.getUser(authDAO.getAuth(command.getAuthString()).username()).username() == null) {
                throw new DataAccessException("User does not exist.\n");
            } else {

                if (Objects.equals(gameDAO.getGame(command.gameID).whiteUsername(), authDAO.getAuth(command.getAuthString()).username()) || Objects.equals(gameDAO.getGame(command.gameID).blackUsername(), authDAO.getAuth(command.getAuthString()).username())) {
                    gameDAO.getGame(command.gameID).game().endGame();
                    gameDAO.updateGame(command.gameID, gameDAO.getGame(command.gameID).whiteUsername(), gameDAO.getGame(command.gameID).blackUsername(), gameDAO.getGame(command.gameID).game());

                    String message = String.format("Player %s has resigned", authDAO.getAuth(command.getAuthString()).username());
                    Notification notification = new Notification(message);

                    connections.removeConnection(command.getAuthString());
                    connections.broadcast(command.getAuthString(), command.gameID, notification);
                } else {
                    throw new DataAccessException("You are not a player in this game.\n");
                }

            }

        } catch (DataAccessException | IOException e) {
            Error error = new Error(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }


}
