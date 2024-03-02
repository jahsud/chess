package model;

import chess.ChessGame;

public record JoinGameData(ChessGame.TeamColor color, Integer gameID) {
}
