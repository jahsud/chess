package webSocketMessages.userCommands;

public class JoinObserver extends UserGameCommand {
    private final Integer gameID;

    public JoinObserver(CommandType type, String authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }

}
