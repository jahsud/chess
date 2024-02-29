package service;

import dataAccess.MemoryAuthDAO;
import dataAccess.exceptions.AlreadyTakenException;
import dataAccess.exceptions.BadRequestException;
import dataAccess.exceptions.DataAccessException;
import dataAccess.MemoryGameDAO;
import dataAccess.exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.ListGamesResult;

public class GameService {
    private final MemoryGameDAO gameDAO;
    private final MemoryAuthDAO authDAO;

    public GameService (MemoryGameDAO gameDAO, MemoryAuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void clear () throws DataAccessException {
        gameDAO.clear();
    }

    public ListGamesResult listGames (ListGamesRequest listGamesRequest) throws DataAccessException, BadRequestException, UnauthorizedException {
        AuthData auth = authDAO.getAuth(listGamesRequest.authToken());
        if (auth == null) {
            throw new BadRequestException("User not logged in");
        }
        if (!auth.authToken().equals(listGamesRequest.authToken())) {
            throw new UnauthorizedException("Invalid auth token");
        }
        return new ListGamesResult(gameDAO.listGames(), null);
    }

    public CreateGameResult createGame (CreateGameRequest createGameRequest) throws DataAccessException, BadRequestException, UnauthorizedException {
        AuthData auth = authDAO.getAuth(createGameRequest.authToken());
        if (createGameRequest.gameName() == null) {
            throw new BadRequestException("Missing fields");
        }
        if (!auth.authToken().equals(createGameRequest.authToken())) {
            throw new UnauthorizedException("Invalid auth token");
        }
        return new CreateGameResult(gameDAO.createGame(createGameRequest.authToken()).gameID(), null);
    }

    public void joinGame (JoinGameRequest joinGameRequest) throws DataAccessException, BadRequestException, UnauthorizedException, AlreadyTakenException {
        AuthData auth = authDAO.getAuth(joinGameRequest.authToken());
        GameData game = gameDAO.getGame(joinGameRequest.gameID());
        if (gameDAO.getGame(joinGameRequest.gameID()) == null) {
            throw new BadRequestException("Game not found");
        }
        if (!auth.authToken().equals(joinGameRequest.authToken())) {
            throw new UnauthorizedException("Invalid auth token");
        }
        if (game.blackUsername() != null || game.whiteUsername() != null{
            throw new AlreadyTakenException("Game is full");
        }
        if (game.whiteUsername() == null) {
            gameDAO.updateGame(joinGameRequest.gameID(), game.whiteUsername());
        }
        else if (game.blackUsername() == null)
            gameDAO.updateGame(joinGameRequest.gameID(), game.blackUsername());
        } else {

        }
    }


}
