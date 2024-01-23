package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {

        Collection<ChessMove> allMoves = new ArrayList<>(); // HashSet<ChesMove> name = new HashSet<>();

        switch (this.getPieceType()) {
            case KING -> kingMove(board, myPosition, allMoves);
            case QUEEN -> queenMove(board, myPosition, allMoves);
            case BISHOP -> bishopMove(board, myPosition, allMoves);
            case KNIGHT -> knightMove(board, myPosition, allMoves);
            case ROOK -> rookMove(board, myPosition, allMoves);
            case PAWN -> pawnMove(board, myPosition, allMoves);
        }

        return allMoves;
    }


    private void kingMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> allMoves) {

        int[][] coordinates = {{}, {}, {}, {}};

    }

    private void queenMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> allMoves) {

        int[][] coordinates = {{}, {}, {}, {}};

    }

    private void bishopMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> allMoves) {

        int[][] coordinates = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};

        for (int[] coordinate : coordinates) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += coordinate[0];
                col += coordinate[1];

                if (validMove(row, col)) {
                    var destination = new ChessPosition(row, col);
                    var atDestination = board.getPiece(destination);

                    if (atDestination == null) {
                        allMoves.add(new ChessMove(myPosition, destination, null));
                    } else if (atDestination.getTeamColor() != this.getTeamColor()) {
                        allMoves.add(new ChessMove(myPosition, destination, null));
                        break;
                    } else {
                        break;
                    }

                } else {
                    break;
                }

            }

        }


    }

    private void knightMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> allMoves) {

        int[][] coordinates = {{}, {}, {}, {}};


    }

    private void rookMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> allMoves) {

        int[][] coordinates = {{}, {}, {}, {}};


    }

    private void pawnMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> allMoves) {

        int[][] coordinates = {{}, {}, {}, {}};


    }


    private boolean validMove(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChessPiece that = (ChessPiece) o;

        if (pieceColor != that.pieceColor) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = pieceColor != null ? pieceColor.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
    }
}
