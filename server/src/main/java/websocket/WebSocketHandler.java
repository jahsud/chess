package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage (Session session, String message) throws IOException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        switch (userGameCommand.getCommandType()) {
            case JOIN_PLAYER -> joinPlayer(userGameCommand.getAuthString(), session);
            case JOIN_OBSERVER -> joinObserver(userGameCommand.getAuthString(), session);
            case MAKE_MOVE -> makeMove(userGameCommand.getAuthString(), session);
            case LEAVE -> leave(userGameCommand.getAuthString());
            case RESIGN -> resign(userGameCommand.getAuthString());
        }
    }

    private void joinPlayer (String player, Session session) {
        connections.addConnection(player, session);
        var message = String.format("Player %s has joined the game", player);
    }

    private void joinObserver (String player, Session session) {
    }

    private void makeMove (String player, Session session) {

    }

    private void leave (String player) {

    }

    private void resign (String player) {

    }


}
