package websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import ui.ResponseException;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Error;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketUri = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketUri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION ->
                                notificationHandler.notify(new Gson().fromJson(message, Notification.class));
                        case LOAD_GAME -> notificationHandler.load(new Gson().fromJson(message, LoadGame.class));
                        case ERROR -> notificationHandler.warn(new Gson().fromJson(message, Error.class));
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinPlayer(String authToken, Integer gameID, String playerColor) throws ResponseException {
        try {
            var command = new JoinPlayer(authToken, gameID, playerColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void joinObserver(String authToken, Integer gameID) throws ResponseException {
        try {
            var command = new JoinObserver(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void leave(String authToken2, Integer gameID2) throws ResponseException {
        try {
            var command = new Leave(authToken2, gameID2);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void move(String authToken, Integer gameID, ChessMove move) throws ResponseException {
        try {
            var command = new MakeMove(authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID) throws ResponseException {
        try {
            var command = new Resign(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

}
