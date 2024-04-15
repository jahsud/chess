package webSocketMessages.userCommands;

public class Resign extends UserGameCommand {
    public Integer gameID;

    public Resign(String authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.RESIGN;
    }

}
