import server.Server;
import ui.Repl;

public class Main {
    public static void main (String[] args) {
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }
        new Server().run(8080);
        new Repl(serverUrl).run();
    }
}