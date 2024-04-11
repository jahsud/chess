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
        connections.addConnection(command.getAuthString(), session);

        //ChessGame game = gameDAO.getGame(command.gameID).game();
        //var loadGame = new LoadGame(game);
        //session.getRemote().sendString(new Gson().toJson(loadGame));

        AuthData auth = authDAO.getAuth(command.getAuthString());
        var message = String.format("%s has joined the game as a %s player", auth.username(), command.playerColor);
        var notification = new Notification(message);

        connections.broadcastNotification(command.getAuthString(), notification);
        //connections.broadcastGame()
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
