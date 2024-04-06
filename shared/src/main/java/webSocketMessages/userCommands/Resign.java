package webSocketMessages.userCommands;

public class Resign extends UserGameCommand {
    public Resign (CommandType type, String authToken, Integer gameID) {
        super(authToken);
    }
}
