package service;

import dataAccess.*;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.ListGamesResult;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException, UnauthorizedException {
        AuthData auth = authDAO.getAuth(listGamesRequest.authToken());
        if (auth == null || !auth.authToken().equals(listGamesRequest.authToken())) {
            throw new UnauthorizedException("Invalid auth token");
        }
        return new ListGamesResult(gameDAO.listGames(), null);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException, BadRequestException, UnauthorizedException {
        AuthData auth = authDAO.getAuth(createGameRequest.authToken());
        if (createGameRequest.gameName() == null || createGameRequest.authToken() == null) {
            throw new BadRequestException("Missing fields");
        }
        if (auth == null || !auth.authToken().equals(createGameRequest.authToken())) {
            throw new UnauthorizedException("Invalid auth token");
        }
        return new CreateGameResult(gameDAO.createGame(createGameRequest.gameName()).gameID(), null);
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException, BadRequestException, UnauthorizedException, AlreadyTakenException {
        AuthData auth = authDAO.getAuth(joinGameRequest.authToken());
        GameData games = gameDAO.getGame(joinGameRequest.gameID());
        if (games == null) {
            throw new BadRequestException("Game not found");
        }
        if (auth == null || !auth.authToken().equals(joinGameRequest.authToken())) {
            throw new UnauthorizedException("Invalid auth token");
        }
        if (joinGameRequest.playerColor() != null) {
            if ((joinGameRequest.playerColor().equals("WHITE") && games.whiteUsername() != null) ||
                    (joinGameRequest.playerColor().equals("BLACK") && games.blackUsername() != null)) {
                throw new AlreadyTakenException("Color is already taken");
            }
            if (joinGameRequest.playerColor().equals("WHITE")) {
                gameDAO.updateGame(joinGameRequest.gameID(), auth.username(), games.blackUsername(), games.game());
            } else {
                gameDAO.updateGame(joinGameRequest.gameID(), games.whiteUsername(), auth.username(), games.game());
            }
        } else {
            // Add user as an observer
            gameDAO.updateGame(joinGameRequest.gameID(), games.whiteUsername(), games.blackUsername(), games.game());
        }
    }


}
