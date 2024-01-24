package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

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

        Collection<ChessMove> allMoves = new ArrayList<>(); // HashSet<ChessMove> allMoves = new HashSet<>();

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

        int[][] coordinates = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};

        for (int[] coordinate : coordinates) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            row += coordinate[0];
            col += coordinate[1];

            if (validMove(row, col)) {
                var destination = new ChessPosition(row, col);
                var atDestination = board.getPiece(destination);

                if (atDestination == null || atDestination.getTeamColor() != this.getTeamColor()) {
                    allMoves.add(new ChessMove(myPosition, destination, null));
                }

            } else {
                break;
            }

        }

    }

    private void queenMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> allMoves) {

        int[][] coordinates = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};

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

    private void rookMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> allMoves) {

        int[][] coordinates = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

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

        int[][] coordinates = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};

        for (int[] coordinate : coordinates) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            row += coordinate[0];
            col += coordinate[1];

            if (validMove(row, col)) {
                var destination = new ChessPosition(row, col);
                var atDestination = board.getPiece(destination);

                if (atDestination == null || atDestination.getTeamColor() != this.getTeamColor()) {
                    allMoves.add(new ChessMove(myPosition, destination, null));
                }

            }

        }

    }

    private void pawnMove(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> allMoves) {

        var color = this.getTeamColor();
        int startRow = myPosition.getRow();

        // Moving and capturing coordinates are different for pawns
        int[][] movingCoordinates;
        int[][] capturingCoordinates;

        // If piece is white, then it moves forward
        if (color == WHITE) {

            if (startRow == 2) {
                // Pawns have the option to move two squares at first move
                movingCoordinates = new int[][]{{1, 0}, {2, 0}};
                pawnMovingRule(board, myPosition, allMoves, movingCoordinates);
            } else {
                // Pawns can only move one square after the first move
                movingCoordinates = new int[][]{{1, 0}};
                pawnMovingRule(board, myPosition, allMoves, movingCoordinates);
            }

            // Forward capturing coordinates work the same way no matter where you start
            capturingCoordinates = new int[][]{{1, -1}, {1, 1}};
            pawnCapturingRule(board, myPosition, allMoves, capturingCoordinates);

        } else { // If piece is black, then it moves backward

            if (startRow == 7) {
                // Pawns have the option to move two squares at first move
                movingCoordinates = new int[][]{{-1, 0}, {-2, 0}};
                pawnMovingRule(board, myPosition, allMoves, movingCoordinates);
            } else {
                // Pawns can only move one square after the first move
                movingCoordinates = new int[][]{{-1, 0}};
                pawnMovingRule(board, myPosition, allMoves, movingCoordinates);
            }

            // Forward capturing coordinates work the same way no matter where you start
            capturingCoordinates = new int[][]{{-1, -1}, {-1, 1}};
            pawnCapturingRule(board, myPosition, allMoves, capturingCoordinates);

        }

    }

    private void pawnMovingRule(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> allMoves, int[][] movingCoordinates) {
        for (int[] coordinate : movingCoordinates) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            row += coordinate[0];
            col += coordinate[1];

            if (validMove(row, col)) {
                var destination = new ChessPosition(row, col);
                var atDestination = board.getPiece(destination);

                if (atDestination == null) {
                    // Check for pawn promotion
                    if ((this.getTeamColor() == WHITE && row == 8) || (this.getTeamColor() == BLACK && row == 1)) {
                        // Promotion; usually to a queen, but can be any piece
                        allMoves.add(new ChessMove(myPosition, destination, PieceType.BISHOP));
                        allMoves.add(new ChessMove(myPosition, destination, PieceType.QUEEN));
                        allMoves.add(new ChessMove(myPosition, destination, PieceType.ROOK));
                        allMoves.add(new ChessMove(myPosition, destination, PieceType.KNIGHT));
                    } else {
                        allMoves.add(new ChessMove(myPosition, destination, null));
                    }
                } else {
                    break;
                }
            }
        }
    }


    private void pawnCapturingRule(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> allMoves, int[][] capturingCoordinates) {
        for (int[] coordinate : capturingCoordinates) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            row += coordinate[0];
            col += coordinate[1];

            if (validMove(row, col)) {
                var destination = new ChessPosition(row, col);
                var atDestination = board.getPiece(destination);

                if (atDestination != null && atDestination.getTeamColor() != this.getTeamColor()) {
                    // Check if the pawn has reached the promotion rank
                    if ((this.getTeamColor() == WHITE && row == 8) || (this.getTeamColor() != WHITE && row == 1)) {
                        // If so, add a move for each promotion option
                        allMoves.add(new ChessMove(myPosition, destination, PieceType.QUEEN));
                        allMoves.add(new ChessMove(myPosition, destination, PieceType.ROOK));
                        allMoves.add(new ChessMove(myPosition, destination, PieceType.BISHOP));
                        allMoves.add(new ChessMove(myPosition, destination, PieceType.KNIGHT));
                    } else {
                        // If not eligible for promotion, add a regular move
                        allMoves.add(new ChessMove(myPosition, destination, null));
                    }
                }
            }
        }
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
        int result = pieceColor.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
