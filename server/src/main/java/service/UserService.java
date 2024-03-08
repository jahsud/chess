package service;

import dataAccess.*;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import request.*;
import result.*;

public class UserService {
    // private final MemoryUserDAO userDAO;
    // private final MemoryAuthDAO authDAO;
    private final MySqlUserDAO userDAO;
    private final MySqlAuthDAO authDAO;

    // public UserService (My userDAO, MemoryAuthDAO authDAO) {
    public UserService (MySqlUserDAO userDAO, MySqlAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void clear () throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
    }

    public RegisterResult register (RegisterRequest registerRequest) throws DataAccessException, BadRequestException, AlreadyTakenException {
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new BadRequestException("Missing fields");
        }
        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new AlreadyTakenException("Username already exists");
        }
        UserData newUser = userDAO.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
        AuthData newAuth = authDAO.createAuth(newUser.username());
        return new RegisterResult(newUser.username(), newAuth.authToken(), null);
    }

    public LoginResult login (LoginRequest loginRequest) throws UnauthorizedException, DataAccessException {
        UserData existingUser = userDAO.getUser(loginRequest.username());
        if (existingUser == null || !userDAO.verifyPassword(existingUser.username(), loginRequest.password())) {
            throw new UnauthorizedException("Invalid password");
        }
        AuthData newAuth = authDAO.createAuth(existingUser.username());
        return new LoginResult(existingUser.username(), newAuth.authToken(), null);
    }

    public void logout (LogoutRequest logoutRequest) throws BadRequestException, UnauthorizedException, DataAccessException {
        AuthData auth = authDAO.getAuth(logoutRequest.authToken());
        if (logoutRequest.authToken() == null) {
            throw new BadRequestException("User not logged in");
        }
        if (auth == null || !auth.authToken().equals(logoutRequest.authToken())) {
            throw new UnauthorizedException("Invalid auth token");
        }
        authDAO.deleteAuth(auth.authToken());
    }

}
