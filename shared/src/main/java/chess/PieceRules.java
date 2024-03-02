package chess;

import java.util.HashSet;

public class PieceRules {
    public static void kingRule (ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor, HashSet<ChessMove> allMoves) {
        // Direction in which king can move
        int[][] coordinates = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        // King can only move one square at a time
        singleMove(board, myPosition, teamColor, coordinates, allMoves);
    }

    public static void knightRule (ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor, HashSet<ChessMove> allMoves) {
        int[][] coordinates = {{2, 1}, {2, -1}, {1, -2}, {1, 2}, {-2, 1}, {-2, -1}, {-1, 2}, {-1, -2}};
        singleMove(board, myPosition, teamColor, coordinates, allMoves);
    }

    public static void queenRule (ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor, HashSet<ChessMove> allMoves) {
        int[][] coordinates = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};
        // Queen can move to multiple squares in the same direction
        multiMove(board, myPosition, teamColor, coordinates, allMoves);
    }

    public static void bishopRule (ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor, HashSet<ChessMove> allMoves) {
        int[][] coordinates = {{1, 1}, {-1, 1}, {-1, -1}, {1, -1}};
        multiMove(board, myPosition, teamColor, coordinates, allMoves);
    }

    public static void rookRule (ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor, HashSet<ChessMove> allMoves) {
        int[][] coordinates = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        multiMove(board, myPosition, teamColor, coordinates, allMoves);
    }

    public static void pawnRule (ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor, HashSet<ChessMove> allMoves) {
        // Pawns move and capture in different directions
        int[][] movingCoordinates;
        int[][] capturingCoordinates;
        int row = myPosition.getRow();

        // Consider different colors for promotions at specific directions pawns can take
        if (teamColor == ChessGame.TeamColor.WHITE) {
            // Pawns can move up to 2 squares only at first turn. Thus, moving coordinates can be different
            if (row == 2) {
                movingCoordinates = new int[][]{{1, 0}, {2, 0}};
                pawnMove(board, myPosition, teamColor, movingCoordinates, allMoves);
            } else {
                movingCoordinates = new int[][]{{1, 0}};
                pawnMove(board, myPosition, teamColor, movingCoordinates, allMoves);
            }
            // Capturing coordinates do not depend on turn
            capturingCoordinates = new int[][]{{1, 1}, {1, -1}};
            pawnCapture(board, myPosition, teamColor, capturingCoordinates, allMoves);
        } else {
            // Pawns can move up to 2 squares only at first turn. Thus, moving coordinates can be different
            if (row == 7) {
                movingCoordinates = new int[][]{{-1, 0}, {-2, 0}};
                pawnMove(board, myPosition, teamColor, movingCoordinates, allMoves);
            } else {
                movingCoordinates = new int[][]{{-1, 0}};
                pawnMove(board, myPosition, teamColor, movingCoordinates, allMoves);
            }
            // Capturing coordinates do not depend on turn
            capturingCoordinates = new int[][]{{-1, 1}, {-1, -1}};
            pawnCapture(board, myPosition, teamColor, capturingCoordinates, allMoves);
        }
    }

    private static void pawnMove (ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor, int[][] movingCoordinates, HashSet<ChessMove> allMoves) {
        // Check every possible coordinate/move
        for (int[] coordinate : movingCoordinates) {
            // Get row and column
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            // Advance according to coordinates
            row += coordinate[0];
            col += coordinate[1];
            // Check if new position is valid (empty). If so, proceed to save move.
            if (validMove(row, col)) {
                var destination = new ChessPosition(row, col);
                var atDestination = board.getPiece(destination);
                if (atDestination == null) {
                    // Check if promotion is available
                    isPromotionAvailable(myPosition, teamColor, allMoves, row, destination);
                } else {
                    break;
                }
            } else {
                break;
            }
        }
    }

    private static void pawnCapture (ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor, int[][] capturingCoordinates, HashSet<ChessMove> allMoves) {
        // Check every possible coordinate/move
        for (int[] coordinate : capturingCoordinates) {
            // Get row and column
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            // Advance according to coordinates
            row += coordinate[0];
            col += coordinate[1];
            // Check if new position is valid (empty). If so, proceed to save move.
            if (validMove(row, col)) {
                var destination = new ChessPosition(row, col);
                var atDestination = board.getPiece(destination);
                if (atDestination != null && atDestination.getTeamColor() != teamColor) {
                    // Check if promotion is available
                    isPromotionAvailable(myPosition, teamColor, allMoves, row, destination);
                    break;
                }
            }
        }
    }

    private static void isPromotionAvailable (ChessPosition myPosition, ChessGame.TeamColor teamColor, HashSet<ChessMove> allMoves, int row, ChessPosition destination) {
        if ((row == 8 && teamColor == ChessGame.TeamColor.WHITE) || (row == 1 && teamColor == ChessGame.TeamColor.BLACK)) {
            allMoves.add(new ChessMove(myPosition, destination, ChessPiece.PieceType.QUEEN));
            allMoves.add(new ChessMove(myPosition, destination, ChessPiece.PieceType.BISHOP));
            allMoves.add(new ChessMove(myPosition, destination, ChessPiece.PieceType.KNIGHT));
            allMoves.add(new ChessMove(myPosition, destination, ChessPiece.PieceType.ROOK));
        } else {
            allMoves.add(new ChessMove(myPosition, destination, null));
        }
    }

    private static void singleMove (ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor, int[][] coordinates, HashSet<ChessMove> allMoves) {
        // Check every possible coordinate/move
        for (int[] coordinate : coordinates) {
            // Get row and column
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            // Advance according to coordinates
            row += coordinate[0];
            col += coordinate[1];
            // Check if new position is valid (empty or with opponent). If so, proceed to save move
            if (validMove(row, col)) {
                var destination = new ChessPosition(row, col);
                var atDestination = board.getPiece(destination);
                if (atDestination == null || atDestination.getTeamColor() != teamColor) {
                    allMoves.add(new ChessMove(myPosition, destination, null));
                }
            }
        }
    }

    private static void multiMove (ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor teamColor, int[][] coordinates, HashSet<ChessMove> allMoves) {
        // Check every possible coordinate/move
        for (int[] coordinate : coordinates) {
            // Get row and column
            int row = myPosition.getRow();
            int col = myPosition.getColumn();
            // Advance according to coordinates
            while (true) {
                row += coordinate[0];
                col += coordinate[1];
                // Check if new position is valid
                if (validMove(row, col)) {
                    var destination = new ChessPosition(row, col);
                    var atDestination = board.getPiece(destination);
                    // If destination is empty proceed to save all possible moves for the same coordinate
                    if (atDestination == null) {
                        allMoves.add(new ChessMove(myPosition, destination, null));
                        // If opponent is at destination, capture, save move, and proceed with next coordinate
                    } else if (atDestination.getTeamColor() != teamColor) {
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

    private static boolean validMove (int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

}
