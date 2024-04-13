package ui;

import chess.*;

import javax.lang.model.type.NullType;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class Board {
    int number = 0;

    private final String[] columns = new String[]{SPACE, "a", "b", "c", "d", "e", "f", "g", "h", SPACE};
    private final String[] rows = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
    //    private final String[][] board = new String[][]{
//            {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK},
//            {WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN, WHITE_PAWN},
//            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
//            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
//            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
//            {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
//            {BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN, BLACK_PAWN},
//            {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK},
//    };

    public void draw(ChessGame game) {

        ChessBoard board = game.getBoard();

        System.out.println(ERASE_SCREEN + RESET_BG_COLOR + SET_TEXT_COLOR_WHITE);

        for (String column : columns) {
            System.out.print(SPACE + column + SPACE);
        }
        System.out.println();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 10; j++) {
                if (j == 0) {
                    System.out.print(SPACE + rows[i] + SPACE);
                } else if (j < 9) {
                    ChessPiece chessPiece = board.getPiece(new ChessPosition(i + 1, j));
                    if (number % 2 == 0) {
                        drawPiece(chessPiece, SET_BG_COLOR_LIGHT_GREY);
                    } else {
                        drawPiece(chessPiece, SET_BG_COLOR_DARK_GREY);
                    }
                } else {
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

    private void drawPiece(ChessPiece chessPiece, String bgColor) {
        switch (chessPiece.getPieceType()) {
            case PAWN -> {
                if (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    System.out.print(bgColor + WHITE_PAWN + RESET_BG_COLOR);
                } else {
                    System.out.print(bgColor + BLACK_PAWN + RESET_BG_COLOR);
                }
            }
            case ROOK -> {
                if (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    System.out.print(bgColor + WHITE_ROOK + RESET_BG_COLOR);
                } else {
                    System.out.print(bgColor + BLACK_ROOK + RESET_BG_COLOR);
                }
            }
            case KNIGHT -> {
                if (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    System.out.print(bgColor + WHITE_KNIGHT + RESET_BG_COLOR);
                } else {
                    System.out.print(bgColor + BLACK_KNIGHT + RESET_BG_COLOR);
                }
            }
            case BISHOP -> {
                if (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    System.out.print(bgColor + WHITE_BISHOP + RESET_BG_COLOR);
                } else {
                    System.out.print(bgColor + BLACK_BISHOP + RESET_BG_COLOR);
                }
            }
            case QUEEN -> {
                if (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    System.out.print(bgColor + WHITE_QUEEN + RESET_BG_COLOR);
                } else {
                    System.out.print(bgColor + BLACK_QUEEN + RESET_BG_COLOR);
                }
            }
            case KING -> {
                if (chessPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    System.out.print(bgColor + WHITE_KING + RESET_BG_COLOR);
                } else {
                    System.out.print(bgColor + BLACK_KING + RESET_BG_COLOR);
                }
            }
            default -> System.out.print(bgColor + EMPTY + RESET_BG_COLOR);
        }
    }


}
