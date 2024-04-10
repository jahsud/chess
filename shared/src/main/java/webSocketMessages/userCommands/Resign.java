package webSocketMessages.userCommands;

public class Resign extends UserGameCommand {
    private final Integer gameID;

    public Resign(CommandType type, String authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }

}
