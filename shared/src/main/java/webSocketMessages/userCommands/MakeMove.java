package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand {
    private final Integer gameID;
    private final ChessMove move;

    public MakeMove(CommandType type, String authToken, Integer gameID, ChessMove move) {
        super(authToken);
        this.gameID = gameID;
        this.move = move;
    }

    public Integer getGameID() {
        return gameID;
    }

    public ChessMove getMove() {
        return move;
    }
    
}
