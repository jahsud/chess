package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMove extends UserGameCommand {
    public MakeMove (CommandType type, String authToken, Integer gameID, ChessMove move) {
        super(authToken);
    }
}
