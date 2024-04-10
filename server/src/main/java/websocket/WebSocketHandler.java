package websocket;

import chess.ChessGame;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.*;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.userCommands.*;
import webSocketMessages.serverMessages.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    GameDAO gameDAO = new MySqlGameDAO();
    UserDAO userDAO = new MySqlUserDAO();

    @OnWebSocketMessage
    public void onMessage (Session session, String message) throws IOException, DataAccessException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer((JoinPlayer) command, session);
            case JOIN_OBSERVER -> joinObserver((JoinObserver) command, session);
            case MAKE_MOVE -> makeMove((MakeMove) command, session);
            case LEAVE -> leave((Leave) command, session);
            case RESIGN -> resign((Resign) command, session);
        }
    }

    private void joinPlayer (JoinPlayer command, Session session) throws IOException, DataAccessException {
        connections.addConnection(command.getAuthString(), session);

        //ChessGame game = gameDAO.getGame(command.getGameID()).game();
        //var loadGame = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, game);
        //session.getRemote().sendString(new Gson().toJson(loadGame));

        UserData user = userDAO.getUser(command.getAuthString());
        var message = String.format("Player %s has joined the game as a %s player", user, command.playerColor);
        var notification = new Notification(message);

        connections.broadcast(command.getAuthString(), notification);
    }

    private void joinObserver (JoinObserver command, Session session) {

    }

    private void makeMove (MakeMove command, Session session) {

    }

    private void leave (Leave command, Session session) {

    }

    private void resign (Resign command, Session session) {

    }


}
