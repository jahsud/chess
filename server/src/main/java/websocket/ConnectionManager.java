package websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void addConnection(String authToken, Session session) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
    }

    public void removeConnection(String authToken) {
        connections.remove(authToken);
    }

    public void broadcastNotification(String excludeAuthToken, Notification message) throws IOException {
        System.out.println("\nBroadcasting message: " + message.message);
        var removeList = new ArrayList<Connection>();
        for (var connection : connections.values()) {
            System.out.println("Checking connection: " + connection.authToken);
            if (connection.session.isOpen()) {
                System.out.println("Connection is open");
                connection.send(message.message);
                if (!connection.authToken.equals(excludeAuthToken)) {
                    System.out.println("Sending message to " + connection.authToken);
                    connection.send(message.message);
                } else {
                    System.out.println("Excluding " + connection.authToken);
                    removeList.add(connection);
                }
            }
        }
        for (var connection : removeList) {
            connections.remove(connection.authToken);
        }
    }

//    public void broadcastGame(String excludeAuthToken, ChessGame game) throws IOException {
//        System.out.println("\nBroadcasting message: " + message.message);
//        var removeList = new ArrayList<Connection>();
//        for (var connection : connections.values()) {
//            System.out.println("Checking connection: " + connection.authToken);
//            if (connection.session.isOpen()) {
//                System.out.println("Connection is open");
//                connection.send(message.message);
//                if (!connection.authToken.equals(excludeAuthToken)) {
//                    System.out.println("Sending message to " + connection.authToken);
//                    connection.send(message.message);
//                } else {
//                    System.out.println("Excluding " + connection.authToken);
//                    removeList.add(connection);
//                }
//            }
//        }
//        for (var connection : removeList) {
//            connections.remove(connection.authToken);
//        }
//    }

}
