package ui;

import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.Error;
import websocket.NotificationHandler;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final Client client;
    private final Board board = new Board();
    private final StringBuilder notifications = new StringBuilder();

    public Repl(String serverUrl) throws ResponseException {
        client = new Client(serverUrl, this);
    }

    public void run() {
        System.out.println(RESET_BG_COLOR + "♕ Welcome to 240 Chess. Type 'help' to get started. ♕");
        printPrompt();

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("Goodbye!")) {

            String input = scanner.nextLine();
            try {
                result = client.processInput(input);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_RED + msg + "\n" + RESET_TEXT_COLOR);
            }
            if (client.isNotificationExpected(input)) {
                try {
                    Thread.sleep(100); // Wait for 5 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                printNotifications();
            } else {
                printPrompt();
            }
        }
        System.out.println();
    }

    @Override
    public void notify(Notification notification) {
        notifications.append("\n").append(SET_TEXT_COLOR_YELLOW).append(notification.message).append("\n").append(SET_TEXT_COLOR_GREEN);
        printNotifications();
    }

    @Override
    public void load(LoadGame game) {
        notifications.append("\n").append(SET_TEXT_COLOR_YELLOW).append("this is the game").append("\n").append(SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void warn(Error errorMessage) {
        notifications.append("\n").append(SET_TEXT_COLOR_RED).append(errorMessage.errorMessage).append("Error: Invalid command").append(errorMessage.errorMessage).append(SET_TEXT_COLOR_GREEN);
    }

    private void printNotifications() {
        System.out.print(notifications.toString());
        notifications.setLength(0);
        printPrompt();
    }

    public void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + "[" + client.getState() + "]" + " >>> " + SET_TEXT_COLOR_GREEN);
    }

}
