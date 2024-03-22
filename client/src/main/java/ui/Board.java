package ui;

public class Board {

    private static final String RESET = EscapeSequences.RESET_TEXT_COLOR;

    // Using the provided piece representations from EscapeSequences
    private static final String[][] INITIAL_BOARD = {
            {EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_KING, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK},
            {EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN},
            {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
            {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
            {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
            {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
            {EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN},
            {EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_QUEEN, EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK}
    };

    public void draw () {
        System.out.println(EscapeSequences.ERASE_SCREEN);
        drawBoard(INITIAL_BOARD, true); // Draw for white at the bottom
        System.out.println("\n\n");
        drawBoard(INITIAL_BOARD, false); // Draw for black at the bottom
    }

    private void drawBoard (String[][] board, boolean whiteAtBottom) {
        String[][] displayBoard = whiteAtBottom ? board : reverseBoard(board);
        for (int y = 0; y < displayBoard.length; y++) {
            for (int x = 0; x < displayBoard[y].length; x++) {
                printTile(displayBoard[y][x], x, y);
            }
            System.out.println(RESET);
        }
        // Print the letters for the columns
        printColumnLetters();
    }

    private void printTile (String piece, int x, int y) {
        String bgColor = (x + y) % 2 == 0 ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_BLACK;
        System.out.print(bgColor + piece + RESET);
    }

    private String[][] reverseBoard (String[][] board) {
        String[][] reversed = new String[board.length][];
        for (int i = 0; i < board.length; i++) {
            reversed[board.length - 1 - i] = board[i].clone();
        }
        return reversed;
    }

    private void printColumnLetters () {
        String bottomLetters = " a  b  c  d  e  f  g  h ";
        String topLetters = new StringBuilder(bottomLetters).reverse().toString();
        System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + bottomLetters + RESET);
        System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY + topLetters + RESET);
    }
}
