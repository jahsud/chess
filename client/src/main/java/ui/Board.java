package ui;

import static ui.EscapeSequences.*;

public class Board {
    // Add chess piece and empty square definitions here
    // For example: public static final String WHITE_PAWN = EscapeSequences.WHITE_PAWN;

    // Define the board with empty spaces and initial piece placements
    private final String[][] board = new String[][]{
            {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK},
            {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
            {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK}
    };

    public void draw () {
        System.out.println(ERASE_SCREEN);
        System.out.println("  a b c d e f g h");  // Column labels
        drawBoardFromPerspective('w');  // White's perspective
        System.out.println("\n");
        drawBoardFromPerspective('b');  // Black's perspective
        System.out.println("  h g f e d c b a");  // Column labels reversed for Black's perspective
    }

    private void drawBoardFromPerspective (char perspective) {
        for (int i = 0; i < 8; i++) {
            int row = perspective == 'w' ? 8 - i : i + 1;  // Row numbers
            System.out.print(row + " ");  // Print row label

            for (int j = 0; j < 8; j++) {
                String square = board[perspective == 'w' ? i : 7 - i][j];
                System.out.print(square + " ");  // Print the piece or empty square
            }

            System.out.println(row);  // Print row label again
        }
    }
}
