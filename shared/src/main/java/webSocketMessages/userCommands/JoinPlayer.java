package webSocketMessages.userCommands;

import chess.ChessGame;

public class JoinPlayer extends UserGameCommand {
    public JoinPlayer (CommandType type, String authToken, Integer gameID, ChessGame.TeamColor playerColor) {
        super(authToken);
    }
}
