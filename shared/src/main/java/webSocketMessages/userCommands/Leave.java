package webSocketMessages.userCommands;

public class Leave extends UserGameCommand {
    private final Integer gameID;

    public Leave(CommandType type, String authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }

}
