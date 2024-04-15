package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.*;
import model.AuthData;
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

            if (command.playerColor == null) {
                throw new DataAccessException("Proper player color must be specified (WHITE/BLACK). Or type \"observe <ID>\" to join as an observer.\n");
            } else if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.\n");
            } else {
                ChessGame game = gameDAO.getGame(command.gameID).game();
                if (game == null) {
                    game = new ChessGame();
                }
                LoadGame loadGame = new LoadGame(game);

                AuthData auth = authDAO.getAuth(command.getAuthString());
                String message = String.format("Player %s has joined the game for the %s team", auth.username(), command.playerColor);
                Notification notification = new Notification(message);

                session.getRemote().sendString(new Gson().toJson(loadGame));
                connections.broadcast(command.getAuthString(), command.gameID, notification);
            }

        } catch (DataAccessException | IOException e) {
            Error error = new Error(e.getMessage());
            try {
                session.getRemote().sendString(new Gson().toJson(error));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void joinObserver(JoinObserver command, Session session) {
        try {
            connections.addConnection(command.getAuthString(), command.gameID, session);

            if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.\n");
            } else {
                ChessGame game = gameDAO.getGame(command.gameID).game();
                if (game == null) {
                    game = new ChessGame();
                }
                LoadGame loadGame = new LoadGame(game);

                AuthData auth = authDAO.getAuth(command.getAuthString());
                String message = String.format("Player %s has joined the game as an observer", auth.username());
                Notification notification = new Notification(message);

                session.getRemote().sendString(new Gson().toJson(loadGame));
                connections.broadcast(command.getAuthString(), command.gameID, notification);
            }

        } catch (DataAccessException | IOException e) {
            Error error = new Error(e.getMessage());
            try {
                session.getRemote().sendString(new Gson().toJson(error));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void makeMove(MakeMove command, Session session) {
        try {

            if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.\n");
            } else {
                ChessGame game = gameDAO.getGame(command.gameID).game();
                if (game == null) {
                    game = new ChessGame();
                }
                LoadGame loadGame = new LoadGame(game);

                AuthData auth = authDAO.getAuth(command.getAuthString());
                String message = String.format("Player %s has made a move", auth.username());
                Notification notification = new Notification(message);

                session.getRemote().sendString(new Gson().toJson(loadGame));
                connections.broadcast(command.getAuthString(), command.gameID, notification);
            }

        } catch (DataAccessException | IOException e) {
            Error error = new Error(e.getMessage());
            try {
                session.getRemote().sendString(new Gson().toJson(error));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void leave(Leave command, Session session) {
        try {

            if (command.gameID == null) {
                throw new DataAccessException("Game ID must be specified.\n");
            } else {
                AuthData auth = authDAO.getAuth(command.getAuthString());
                String message = String.format("Player %s has left the game", auth.username());
                Notification notification = new Notification(message);

                connections.removeConnection(command.getAuthString(), command.gameID, session);
                connections.broadcast(command.getAuthString(), command.gameID, notification);
            }

        } catch (DataAccessException | IOException e) {
            Error error = new Error(e.getMessage());
            try {
                session.getRemote().sendString(new Gson().toJson(error));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void resign(Resign command, Session session) {

    }


}
