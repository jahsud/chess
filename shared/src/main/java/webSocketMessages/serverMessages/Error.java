package webSocketMessages.serverMessages;

public class Error extends ServerMessage {
    public Error (ServerMessageType type, String errorMessage) {
        super(type);
    }
}
