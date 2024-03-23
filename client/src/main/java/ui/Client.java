package ui;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    private State state = State.LOGGED_OUT;
    private String authToken;
    private final Board board = new Board();

    public Client (String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String processInput (String input) {
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
                case "logout" -> logout();
                case "quit" -> quit();
                default -> help();
            };
        } catch (ResponseException e) {
            return e.toString();
        }
    }

    public String register (String... params) throws ResponseException {
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

    public String login (String... params) throws ResponseException {
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

    public String logout () throws ResponseException {
        assertSignedIn();
        server.logout(authToken);

        state = State.LOGGED_OUT;
        return "Logged out\n";
    }

    public String observe (String... params) throws ResponseException {
        if (params.length >= 1) {
            assertSignedIn();
            var gameID = params[0];
            server.observe(authToken, Integer.valueOf(gameID));
            board.draw();
            return "Observing game " + gameID + "\n";
        }
        throw new ResponseException(400, "Expected: <ID>\n");
    }

    public String join (String... params) throws ResponseException {
        if (params.length >= 1) {
            assertSignedIn();
            var gameID = params[0];
            var playerColor = (params.length >= 2 && (params[1].equals("white") || params[1].equals("black"))) ? params[1].toUpperCase() : null;
            server.joinGame(authToken, playerColor, Integer.valueOf(gameID));
            board.draw();
            return "Joined game " + gameID + " as " + (playerColor != null ? playerColor : "an observer") + "\n";
        }
        throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK|<empty>]\n");
    }

    public String list () throws ResponseException {
        assertSignedIn();
        var games = server.listGames(authToken).games();
        var result = new StringBuilder();
        for (var game : games) {
            result.append("Game ID: ").append(game.gameID()).append("\t| Game name: ").append(game.gameName()).append("\t| White player: ").append(game.whiteUsername()).append("\t| Black player: ").append(game.blackUsername()).append("\n");
        }
        return result.toString();
    }

    public String create (String... params) throws ResponseException {
        if (params.length >= 1) {
            assertSignedIn();
            var gameName = params[0];
            var gameID = server.createGame(authToken, gameName).gameID();
            return "Game created with ID: " + gameID + "\n";
        }
        throw new ResponseException(400, "Expected: <NAME>\n");
    }


    public State getState () {
        return state;
    }

    private void assertSignedIn () throws ResponseException {
        if (state == State.LOGGED_OUT) {
            throw new ResponseException(400, "You must login\n");
        }
    }

    public String quit () {
        return "Goodbye!";
    }

    public String help () {
        if (state == State.LOGGED_OUT) {
            return SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "to create an account\n" +
                    SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD>" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "to play chess\n" +
                    SET_TEXT_COLOR_BLUE + "quit" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "playing chess\n" +
                    SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "with possible commands\n";
        } else {
            return SET_TEXT_COLOR_BLUE + "create <NAME>" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "a game\n" +
                    SET_TEXT_COLOR_BLUE + "list" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "games\n" +
                    SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK|<empty>]" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "a game\n" +
                    SET_TEXT_COLOR_BLUE + "observe <ID>" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "a game\n" +
                    SET_TEXT_COLOR_BLUE + "logout" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "games\n" +
                    SET_TEXT_COLOR_BLUE + "quit" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "playing chess\n" +
                    SET_TEXT_COLOR_BLUE + "help" + SET_TEXT_COLOR_WHITE + " - " + SET_TEXT_COLOR_MAGENTA + "with possible commands\n";
        }
    }
}
