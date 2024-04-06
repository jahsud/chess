package webSocketMessages.userCommands;

public class Leave extends UserGameCommand {
    public Leave (CommandType type, String authToken, Integer gameID) {
        super(authToken);
    }
}
