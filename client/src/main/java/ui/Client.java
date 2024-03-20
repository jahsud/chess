package ui;

import java.util.Arrays;

import static ui.EscapeSequences.*;

public class Client {
    private final ServerFacade server;
    private final State state = State.LOGGED_OUT;

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
                case "quit" -> quit();
                default -> help();
            };
        } catch (ResponseException e) {
            return e.toString();
        }
    }

    public String register (String... params) throws ResponseException {
        if (params.length != 3) {
            throw new ResponseException("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
        }
        var username = params[0];
        var password = params[1];
        var email = params[2];
        server.register(username, password, email);
        return "Logged in as " + username + "\n";
    }

    public String login (String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException("Usage: login <USERNAME> <PASSWORD>");
        }
        var username = params[0];
        var password = params[1];
        server.login(username, password);
        return "Logged in as " + username + "\n";
    }

    public String quit () {
        return "Goodbye!\n";
    }

    public String help () {
        if (state == State.LOGGED_OUT) {
            return SET_TEXT_COLOR_BLUE + "register <USERNAME> <PASSWORD> <EMAIL>" + RESET_TEXT_COLOR + " - " + SET_TEXT_COLOR_MAGENTA + "to create an account\n" +
                    SET_TEXT_COLOR_BLUE + "login <USERNAME> <PASSWORD>" + RESET_TEXT_COLOR + " - " + SET_TEXT_COLOR_MAGENTA + "to play chess\n" +
                    SET_TEXT_COLOR_BLUE + "quit" + RESET_TEXT_COLOR + " - " + SET_TEXT_COLOR_MAGENTA + "playing chess\n" +
                    SET_TEXT_COLOR_BLUE + "help" + RESET_TEXT_COLOR + " - " + SET_TEXT_COLOR_MAGENTA + "with possible commands\n";
        } else {
            return "Hi there!\n";
        }
    }
}
