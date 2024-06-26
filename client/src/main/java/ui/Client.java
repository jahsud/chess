package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    private final WebSocketFacade webSocket;
    private State state = State.LOGGED_OUT;
    private String authToken;
    private ChessGame.TeamColor teamColor;
    private Integer gameID;

    public Client(String serverUrl, NotificationHandler notificationHandler) throws ResponseException {
        server = new ServerFacade(serverUrl);
        webSocket = new WebSocketFacade(serverUrl, notificationHandler);
    }

    public String processInput(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "redraw" -> redraw();
                case "leave" -> leave();
                case "move" -> move(params);
                case "resign" -> resign();
                case "highlight" -> highlight(params);
                case "logout" -> logout();
                case "quit" -> quit();
                case "clear" -> clear();
                default -> help();
            };
        } catch (ResponseException e) {
            return e.toString();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            authToken = server.register(username, password, email).authToken();
            state = State.LOGGED_IN;
            return "Logged in as " + username + "\n";
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>\n");
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            var username = params[0];
            var password = params[1];
            var loginData = server.login(username, password);
            authToken = loginData.authToken();
            state = State.LOGGED_IN;
            return loginData.message();
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>\n");
    }

    public String logout() throws ResponseException {
        assertSignedIn();
        server.logout(authToken);
        state = State.LOGGED_OUT;
        return "Logged out\n";
    }

    public String observe(String... params) throws ResponseException {
        if (params.length >= 1) {
            assertSignedIn();
            gameID = Integer.valueOf(params[0]);
            server.observe(authToken, gameID);
            webSocket.joinObserver(authToken, gameID);
            state = State.IN_GAME;
            return "Observing game " + gameID + "\n";
        }
        throw new ResponseException(400, "Expected: <ID>\n");
    }

    public String join(String... params) throws ResponseException {
        if (params.length >= 1) {
            assertSignedIn();
            gameID = Integer.valueOf(params[0]);
            var playerColor = (params.length >= 2 && (params[1].equals("white") || params[1].equals("black"))) ? params[1].toUpperCase() : null;
            teamColor = playerColor != null ? ChessGame.TeamColor.valueOf(playerColor) : null;
            if (playerColor != null) {
                server.joinGame(authToken, playerColor, gameID);
                webSocket.joinPlayer(authToken, gameID, playerColor);
                state = State.IN_GAME;
                return "Joined game " + gameID + " in the  " + playerColor + " team\n";
            }
            webSocket.joinPlayer(authToken, gameID, null);
            return "";
        }
        throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK|<empty>]\n");
    }

    public String list() throws ResponseException {
        assertSignedIn();
        var games = server.listGames(authToken).games();
        var result = new StringBuilder();
        for (var game : games) {
            result.append("Game ID: ").append(game.gameID()).append("\t| Game name: ").append(game.gameName()).append("\t| White player: ").append(game.whiteUsername()).append("\t| Black player: ").append(game.blackUsername()).append("\n");
        }
        return result.toString();
    }

    public String create(String... params) throws ResponseException {
        if (params.length >= 1) {
            assertSignedIn();
            var gameName = params[0];
            var gameID = server.createGame(authToken, gameName).gameID();
            return "Game created with ID: " + gameID + "\n";
        }
        throw new ResponseException(400, "Expected: <NAME>\n");
    }

    public String redraw() {
        Board.redraw(teamColor);
        return "";
    }

    public String leave() throws ResponseException {
        assertSignedIn();
        if (state != State.IN_GAME) {
            throw new ResponseException(400, "You're not playing a game\n");
        }
        webSocket.leave(authToken, gameID);
        state = State.LOGGED_IN;
        return "Left game " + gameID + "\n";
    }

    public String move(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length >= 2) {
            ChessPosition start = parseChessPosition(params[0]);
            ChessPosition end = parseChessPosition(params[2]);
            ChessMove move = new ChessMove(start, end, null);
            webSocket.move(authToken, gameID, move);
            return "Moved from " + params[0] + " to " + params[2] + "\n";
        }
        throw new ResponseException(400, "Expected: <start> to <end>\n");
    }

    public String resign() throws ResponseException {
        assertSignedIn();
        if (state == State.IN_GAME) {
            webSocket.resign(authToken, gameID);
            state = State.LOGGED_IN;
            teamColor = null;
            return "Resigned\n";
        } else {
            throw new ResponseException(400, "You're not playing a game\n");
        }
    }

    public String highlight(String... params) throws ResponseException {
        if (state != State.IN_GAME) {
            throw new ResponseException(400, "You're not playing a game\n");
        }
        if (params.length >= 1) {
            ChessPosition position = parseChessPosition(params[0]);
            Board.highlight(teamColor, position);
            return "";
        }
        throw new ResponseException(400, "Expected: <position>\n");
    }

    public State getState() {
        return state;
    }

    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    public ChessPosition parseChessPosition(String input) {
        char colChar = Character.toLowerCase(input.charAt(0));
        int col = colChar - 'a' + 1;
        int row = Character.getNumericValue(input.charAt(1));
        return new ChessPosition(row, col);
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGED_OUT) {
            throw new ResponseException(400, "You must login\n");
        }
    }

    private String clear() throws ResponseException {
        server.clear();
        return "";
    }

    public String quit() {
        return "Goodbye!";
    }

    public String help() {
        if (state == State.LOGGED_OUT) {
            return SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "to create an account\n" +
                    SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD>" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "to play chess\n" +
                    SET_TEXT_COLOR_BLUE + "quit" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "playing chess\n" +
                    SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "with possible commands\n";
        } else if (state == State.LOGGED_IN) {
            return SET_TEXT_COLOR_BLUE + "create <NAME>" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "a game\n" +
                    SET_TEXT_COLOR_BLUE + "list" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "games\n" +
                    SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK|<empty>]" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "a game\n" +
                    SET_TEXT_COLOR_BLUE + "observe <ID>" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "a game\n" +
                    SET_TEXT_COLOR_BLUE + "logout" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "games\n" +
                    SET_TEXT_COLOR_BLUE + "quit" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "playing chess\n" +
                    SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "with possible commands\n";
        } else {
            return SET_TEXT_COLOR_BLUE + "redraw" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "chess board\n" +
                    SET_TEXT_COLOR_BLUE + "leave" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "game\n" +
                    SET_TEXT_COLOR_BLUE + "move <start> to <end>" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "position\n" +
                    SET_TEXT_COLOR_BLUE + "resign" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "game\n" +
                    SET_TEXT_COLOR_BLUE + "highlight <start>" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "legal moves\n" +
                    SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "with possible commands\n";
        }
    }

    public boolean isNotificationExpected(String input) {
        var tokens = input.toLowerCase().split(" ");
        var cmd = (tokens.length > 0) ? tokens[0] : "help";
        return cmd.equals("join") || cmd.equals("observe") || cmd.equals("move");
    }

}
