package websocket;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.Notification;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void addConnection (String authToken, Session session) {
        var connection = new Connection(authToken, session);
        connections.put(authToken, connection);
    }

    public void removeConnection (String authToken) {
        connections.remove(authToken);
    }

    public void broadcast (String excludeAuthToken, Notification notification) throws Exception {
        var removeList = new ArrayList<Connection>();
        for (var connection : connections.values()) {
            if (connection.session.isOpen()) {
                if (!connection.authToken.equals(excludeAuthToken)) {
                    connection.send(notification.toString());
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
