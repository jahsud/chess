package websocket;

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

    public void broadcast(String excludeAuthToken, Notification message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var connection : connections.values()) {
            if (connection.session.isOpen()) {
                if (!connection.authToken.equals(excludeAuthToken)) {
                    connection.send(message.toString());
                } else {
                    removeList.add(connection);
                }
            }
        }
        for (var connection : removeList) {
            connections.remove(connection.authToken);
        }
    }
}
