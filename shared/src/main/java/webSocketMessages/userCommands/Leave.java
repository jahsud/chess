package webSocketMessages.userCommands;

public class Leave extends UserGameCommand {
    public Integer gameID;

    public Leave(String authToken, Integer gameID) {
        super(authToken);
        this.gameID = gameID;
    }

}
