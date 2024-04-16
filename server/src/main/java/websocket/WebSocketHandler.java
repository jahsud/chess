package websocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.userCommands.*;
import webSocketMessages.serverMessages.*;

import java.io.IOException;

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

    private void joinPlayer(JoinPlayer command, Session session) throws IOException, DataAccessException {
        try {
            connections.addConnection(command.getAuthString(), command.gameID, session);

            if (command.getAuthString() == null) {
                throw new DataAccessException("Auth token must be specified.\n");
            } else if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.\n");
            } else if (command.playerColor == null || (!command.playerColor.equals("WHITE") && !command.playerColor.equals("BLACK"))) {
                throw new DataAccessException("Proper player color must be specified (WHITE/BLACK). Or type \"observe <ID>\" to join as an observer.\n");
            }

            GameData game = gameDAO.getGame(command.gameID);
            AuthData auth = authDAO.getAuth(command.getAuthString());
            UserData user = userDAO.getUser(auth.username());

            if (game == null) {
                throw new DataAccessException("Game does not exist.\n");
            } else if (game.game() == null) {
                throw new DataAccessException("Game has not started yet.\n");
            } else if (auth.authToken() == null) {
                throw new DataAccessException("Invalid auth token.\n");
            } else if (user.username() == null) {
                throw new DataAccessException("User does not exist.\n");
            } else if (game.whiteUsername() != null && command.playerColor.equals("WHITE")) {
                throw new DataAccessException("White player is already taken.\n");
            } else if (game.blackUsername() != null && command.playerColor.equals("BLACK")) {
                throw new DataAccessException("Black player is already taken.\n");
            } else {

                LoadGame loadGame = new LoadGame(game.game());

                String message = String.format("Player %s has joined the game for the %s team", auth.username(), command.playerColor);
                Notification notification = new Notification(message);

                session.getRemote().sendString(new Gson().toJson(loadGame));
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
            }

            GameData game = gameDAO.getGame(command.gameID);
            AuthData auth = authDAO.getAuth(command.getAuthString());
            UserData user = userDAO.getUser(auth.username());

            if (game == null) {
                throw new DataAccessException("Game does not exist.\n");
            } else if (auth.authToken() == null) {
                throw new DataAccessException("Invalid auth token.\n");
            } else if (user.username() == null) {
                throw new DataAccessException("User does not exist.\n");
            } else {

                LoadGame loadGame = new LoadGame(game.game());

                String message = String.format("Player %s has joined the game as an observer", auth.username());
                Notification notification = new Notification(message);

                session.getRemote().sendString(new Gson().toJson(loadGame));
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
            }

            GameData game = gameDAO.getGame(command.gameID);
            AuthData auth = authDAO.getAuth(command.getAuthString());
            UserData user = userDAO.getUser(auth.username());

            if (game == null) {
                throw new DataAccessException("Game does not exist.\n");
            } else if (auth.authToken() == null) {
                throw new DataAccessException("Invalid auth token.\n");
            } else if (user.username() == null) {
                throw new DataAccessException("User does not exist.\n");
            } else if (game.game() == null) {
                throw new DataAccessException("Game has not started yet.\n");
            } else if (game.game().getTeamTurn().toString().equals("WHITE") && !game.whiteUsername().equals(auth.username())) {
                throw new DataAccessException("It is not your turn.\n");
            } else if (game.game().getTeamTurn().toString().equals("BLACK") && !game.blackUsername().equals(auth.username())) {
                throw new DataAccessException("It is not your turn.\n");
            } else {

                game.game().makeMove(command.move);

                LoadGame loadGame = new LoadGame(game.game());
                String message = String.format("Player %s has made a move", auth.username());
                Notification notification = new Notification(message);

                session.getRemote().sendString(new Gson().toJson(loadGame));
                connections.broadcast(command.getAuthString(), command.gameID, notification);
            }

        } catch (DataAccessException | IOException | InvalidMoveException e) {
            Error error = new Error(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void leave(Leave command, Session session) throws IOException {
        try {

            if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.\n");
            } else {
                AuthData auth = authDAO.getAuth(command.getAuthString());
                String message = String.format("Player %s has left the game", auth.username());
                Notification notification = new Notification(message);

                connections.removeConnection(command.getAuthString());
                connections.broadcast(command.getAuthString(), command.gameID, notification);
            }

        } catch (DataAccessException | IOException e) {
            Error error = new Error(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void resign(Resign command, Session session) {

    }


}
