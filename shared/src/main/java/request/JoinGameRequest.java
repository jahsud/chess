package request;

import chess.ChessGame;
import chess.ChessPiece;

public record JoinGameRequest(ChessGame.TeamColor color, String gameName) {
}
