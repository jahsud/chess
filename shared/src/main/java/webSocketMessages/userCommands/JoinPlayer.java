package webSocketMessages.userCommands;

public class JoinPlayer extends UserGameCommand {
    private final Integer gameID;
    private final String playerColor;

    public JoinPlayer(CommandType type, String authToken, Integer gameID, String playerColor) {
        super(authToken);
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public Integer getGameID() {
        return gameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }
    
}
