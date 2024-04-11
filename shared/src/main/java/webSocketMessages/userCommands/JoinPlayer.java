package webSocketMessages.userCommands;

public class JoinPlayer extends UserGameCommand {
    public Integer gameID;
    public String playerColor;

    public JoinPlayer(String authToken, Integer gameID, String playerColor) {
        super(authToken);
        this.gameID = gameID;
        this.playerColor = playerColor;
        this.commandType = CommandType.JOIN_PLAYER;
    }

}
