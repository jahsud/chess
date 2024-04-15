package webSocketMessages.userCommands;

public class JoinObserver extends UserGameCommand {
    public Integer gameID;

    public JoinObserver(String authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.JOIN_OBSERVER;
    }

}
