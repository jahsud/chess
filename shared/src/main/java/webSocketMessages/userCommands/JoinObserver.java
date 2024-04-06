package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinObserver extends UserGameCommand {
    public JoinObserver (CommandType type, String authToken, Integer gameID) {
        super(authToken);
    }
}
