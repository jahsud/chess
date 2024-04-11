package ui;

import webSocketMessages.serverMessages.Notification;
import websocket.NotificationHandler;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final Client client;

    public Repl(String serverUrl) throws ResponseException {
        client = new Client(serverUrl, this);
    }

    public void run() {
        System.out.println(RESET_BG_COLOR + "♕ Welcome to 240 Chess. Type 'help' to get started. ♕");

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("Goodbye!")) {
            printPrompt();
            String input = scanner.nextLine();

            try {
                result = client.processInput(input);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED + msg + "\n" + RESET_TEXT_COLOR);
            }
        }
        System.out.println();
    }

    @Override
    public void notify(Notification notification) {
        System.out.print(SET_TEXT_COLOR_RED + notification.message + "\n" + SET_TEXT_COLOR_GREEN);
        printPrompt();
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + "[" + client.getState() + "]" + " >>> " + SET_TEXT_COLOR_GREEN);
    }
}
