package ui;

import static ui.EscapeSequences.*;

public class Board {
    int number = 0;

    private final String[] columns = new String[]{SPACE, "a", "b", "c", "d", "e", "f", "g", "h", SPACE};
    private final String[] rows = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
    private final String[][] board = new String[][]{
            {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK},
            {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
            {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
            {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK},
    };

    public void draw() {
        System.out.println(ERASE_SCREEN + RESET_BG_COLOR + SET_TEXT_COLOR_WHITE);

        // Reverse board
        for (int i = columns.length - 1; i >= 0; i--) {
            System.out.print(SPACE + columns[i] + SPACE);
        }
        System.out.println();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (j == 0) {
                    System.out.print(SPACE + rows[i] + SPACE);
                }
                if (number % 2 == 0) {
                    System.out.print(SET_BG_COLOR_LIGHT_GREY + board[i][j] + RESET_BG_COLOR);
                } else {
                    System.out.print(SET_BG_COLOR_DARK_GREY + board[i][j] + RESET_BG_COLOR);
                }
                if (j == board[i].length - 1) {
                    System.out.print(SPACE + rows[i] + SPACE);
                }
                number++;
            }
            System.out.println();
            number++;
        }
        for (int i = columns.length - 1; i >= 0; i--) {
            System.out.print(SPACE + columns[i] + SPACE);
        }
        System.out.println("\n");

        // Forward board
        for (String column : columns) {
            System.out.print(SPACE + column + SPACE);
        }
        System.out.println();
        for (int i = board.length - 1; i >= 0; i--) {
            for (int j = board[i].length - 1; j >= 0; j--) {
                if (j == board[i].length - 1) {
                    System.out.print(SPACE + rows[i] + SPACE);
                }
                if (number % 2 == 0) {
                    System.out.print(SET_BG_COLOR_LIGHT_GREY + board[i][j] + RESET_BG_COLOR);
                } else {
                    System.out.print(SET_BG_COLOR_DARK_GREY + board[i][j] + RESET_BG_COLOR);
                }
                if (j == 0) {
                    System.out.print(SPACE + rows[i] + SPACE);
                }
                number++;
            }
            System.out.println();
            number++;
        }
        for (String column : columns) {
            System.out.print(SPACE + column + SPACE);
        }
        System.out.println();
    }

}
