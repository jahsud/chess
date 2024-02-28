import server.Server;
import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryGameDAO;
import dataAccess.MemoryUserDAO;
import chess.ChessPiece;
import chess.ChessGame;

public class Main {
    public static void main (String[] args) {
        // Create instances of the required DAOs
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();

        // Pass the DAO instances to the Server constructor
        Server server = new Server(userDAO, gameDAO, authDAO);
        server.run(8080);

        // The rest of your main method remains unchanged
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
    }
}
