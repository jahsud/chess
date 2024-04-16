package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.*;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void addConnection(String authToken, Integer gameID, Session session) {
        var connection = new Connection(authToken, gameID, session);
        connections.put(authToken, connection);
    }

    public void removeConnection(String authToken) {
        connections.remove(authToken);
    }

    public void broadcast(String excludeAuthToken, Integer gameID, Notification message) throws IOException {
        for (var connection : connections.values()) {
            if (connection.session.isOpen() && !connection.authToken.equals(excludeAuthToken) && Objects.equals(connection.gameID, gameID)) {
                connection.send(new Gson().toJson(message));
            }
        }
    }

}
