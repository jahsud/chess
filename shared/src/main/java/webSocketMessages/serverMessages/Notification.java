package webSocketMessages.serverMessages;

public class Notification extends ServerMessage {
    public Notification (ServerMessageType type, String message) {
        super(type);
    }

    public String getMessage () {
        return null;
    }
}
