package ui;

import chess.*;

import static ui.EscapeSequences.*;

public class Board {
    static int number = 0;

    private static final String[] columns = new String[]{SPACE, "a", "b", "c", "d", "e", "f", "g", "h", SPACE};
    private static final String[] rows = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};

    private static ChessGame currentGame;

    public static void draw(ChessGame game, ChessGame.TeamColor teamColor) {

        currentGame = game;

        ChessBoard board = game.getBoard();

        ChessGame.TeamColor teamTurn = game.getTeamTurn();

        if (teamColor == ChessGame.TeamColor.WHITE) {
            drawWhites(board);
        } else {
            drawBlacks(board);
        }

    }

    private static void drawWhites(ChessBoard board) {
        System.out.println(ERASE_SCREEN + RESET_BG_COLOR + SET_TEXT_COLOR_WHITE);

        for (String column : columns) {
            System.out.print(SPACE + column + SPACE);
        }
        System.out.println();

        for (int i = 8; i >= 1; i--) {
            for (int j = 0; j <= 9; j++) {
                if (j == 0) {
                    System.out.print(SPACE + rows[i - 1] + SPACE);
                } else if (j < 9) {
                    ChessPiece chessPiece = board.getPiece(new ChessPosition(i, j));
                    if (number % 2 == 0) {
                        drawPiece(chessPiece, SET_BG_COLOR_LIGHT_GREY);
                    } else {
                        drawPiece(chessPiece, SET_BG_COLOR_DARK_GREY);
                    }
                } else {
                    System.out.print(SPACE + rows[i - 1] + SPACE);
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

    private static void drawBlacks(ChessBoard board) {
        System.out.println(ERASE_SCREEN + RESET_BG_COLOR + SET_TEXT_COLOR_WHITE);

        for (int i = columns.length - 1; i >= 0; i--) {
            System.out.print(SPACE + columns[i] + SPACE);
        }
        System.out.println();

        for (int i = 1; i < 9; i++) {
            for (int j = 9; j >= 0; j--) {
                if (j == 9) {
                    System.out.print(SPACE + rows[i - 1] + SPACE);
                } else if (j > 0) {
                    ChessPiece chessPiece = board.getPiece(new ChessPosition(i, j));
                    if (number % 2 == 0) {
                        drawPiece(chessPiece, SET_BG_COLOR_LIGHT_GREY);
                    } else {
                        drawPiece(chessPiece, SET_BG_COLOR_DARK_GREY);
                    }
                } else {
                    System.out.print(SPACE + rows[i - 1] + SPACE);
                }
                number++;
            }
            System.out.println();
            number++;
        }

        for (int i = columns.length - 1; i >= 0; i--) {
            System.out.print(SPACE + columns[i] + SPACE);
        }
        System.out.println();

    }

    private static void drawPiece(ChessPiece chessPiece, String bgColor) {
        if (chessPiece == null) {
            System.out.print(bgColor + EMPTY + RESET_BG_COLOR);
        } else {
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
            }
        }
    }

    public static void redraw() {
        draw(currentGame, currentGame.getTeamTurn());
    }

}
