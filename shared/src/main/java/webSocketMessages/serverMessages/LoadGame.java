package webSocketMessages.serverMessages;

import model.GameData;

public class LoadGame extends ServerMessage {
    public LoadGame (ServerMessageType type, GameData game) {
        super(type);
    }
}
