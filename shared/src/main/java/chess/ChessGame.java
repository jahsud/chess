package chess;

import java.util.Collection;
import java.util.HashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {


    private ChessBoard board;
    private TeamColor team;
    private TeamColor teamTurn;


    public ChessGame () {
        // Initialize the game with the default starting board
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE; // Set the initial turn to white
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn () {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn (TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves (ChessPosition startPosition) {
        ChessPiece piece = getBoard().getPiece(startPosition);

        if (piece == null || piece.getTeamColor() != getTeamTurn()) {
            return null; // No piece at position or not the piece's turn.
        }

        Collection<ChessMove> potentialMoves = piece.pieceMoves(getBoard(), startPosition);
        HashSet<ChessMove> legalMoves = new HashSet<>();

        for (ChessMove move : potentialMoves) {
            // Simulate the move on a temporary board to check if it's legal.
            ChessBoard tempBoard = getBoard().copy(); // Assumes a clone method is available.

            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();

            ChessPiece movingPiece = tempBoard.getPiece(start);
            tempBoard.addPiece(end, movingPiece);
            tempBoard.addPiece(start, null);

            // Add handling for special moves (e.g., pawn promotion, castling) if necessary.
            if (!isInCheck(piece.getTeamColor(), tempBoard)) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove (ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> legalMoves = validMoves(move.getStartPosition());

        if (legalMoves != null && legalMoves.contains(move)) {
            // Execute the move on the actual board.
            ChessPiece movingPiece = getBoard().getPiece(move.getStartPosition());
            getBoard().addPiece(move.getEndPosition(), movingPiece);
            getBoard().addPiece(move.getStartPosition(), null);
            // Additional handling for special moves as needed.

            // Switch turn.
            setTeamTurn(getTeamTurn() == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        } else {
            throw new InvalidMoveException("Illegal move attempted.");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck (TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        TeamColor opponentColor = (teamColor == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;

        // Check if any opponent piece has a valid move to the king's position
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = getBoard().getPiece(currentPosition);

                if (currentPiece != null && currentPiece.getTeamColor() == opponentColor) {
                    Collection<ChessMove> moves = currentPiece.pieceMoves(getBoard(), currentPosition);

                    if (moves != null && moves.stream().anyMatch(move -> move.getEndPosition().equals(kingPosition))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate (TeamColor teamColor) {
        ChessPosition kingPosition = findKingPosition(teamColor);
        Collection<ChessMove> kingMoves = validMoves(kingPosition);

        return isInCheck(teamColor) && (kingMoves == null || kingMoves.isEmpty());
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate (TeamColor teamColor) {
        // If the player is in check, it's not a stalemate.
        if (isInCheck(teamColor)) {
            return false;
        }

        // Iterate over all pieces of the current team and check if any of them have legal moves.
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = getBoard().getPiece(currentPosition);

                // If the piece belongs to the team and has at least one legal move, it's not a stalemate.
                if ((currentPiece != null) && (currentPiece.getTeamColor() == teamColor)) {
                    Collection<ChessMove> moves = validMoves(currentPosition);
                    if (moves != null && !moves.isEmpty()) {
                        return false; // Found at least one legal move, so it's not a stalemate.
                    }
                }
            }
        }

        // No legal moves found for any piece, it's a stalemate.
        return true;
    }
    }

    private ChessPosition findKingPosition (TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition currentPosition = new ChessPosition(row, col);
                ChessPiece currentPiece = getBoard().getPiece(currentPosition);

                if (currentPiece != null && currentPiece.getPieceType() == ChessPiece.PieceType.KING
                        && currentPiece.getTeamColor() == teamColor) {
                    return currentPosition;
                }
            }
        }

        // The king should always be on the board, so this should not happen
        throw new IllegalStateException("King not found on the board.");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard (ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard () {
        return board;
    }

}
