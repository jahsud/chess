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
                throw new DataAccessException("Player color must be specified. Or you can type \"observe <ID>\" to join as an observer.");
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
        } catch (DataAccessException e) {
            Error error = new Error(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void joinObserver(JoinObserver command, Session session) {

    }

    private void makeMove(MakeMove command, Session session) {

    }

    private void leave(Leave command, Session session) {

    }

    private void resign(Resign command, Session session) {

    }


}
