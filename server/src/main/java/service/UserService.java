package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import request.*;
import result.*;

public class UserService {
    private final MemoryUserDAO userDAO;
    private final MemoryAuthDAO authDAO;

    public UserService (MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public void clear () throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
    }

    public RegisterResult register (RegisterRequest userData) throws DataAccessException, BadRequestException, AlreadyTakenException {
        if (userData.username() == null || userData.password() == null || userData.email() == null) {
            throw new BadRequestException("Missing fields");
        }
        if (userDAO.getUser(userData.username()) != null) {
            throw new AlreadyTakenException("Username already exists");
        }
        UserData newUser = userDAO.createUser(userData.username(), userData.password(), userData.email());
        AuthData newAuth = authDAO.createAuth(newUser.username());
        return new RegisterResult(newUser.username(), newAuth.authToken(), null);
    }

    public LoginResult login (LoginRequest userData) throws UnauthorizedException, DataAccessException {
        UserData existingUser = userDAO.getUser(userData.username());
        if (existingUser == null) {
            throw new UnauthorizedException("User not found");
        }
        if (!existingUser.password().equals(userData.password())) {
            throw new UnauthorizedException("Invalid password");
        }
        AuthData newAuth = authDAO.createAuth(existingUser.username());
        return new LoginResult(existingUser.username(), newAuth.authToken(), null);
    }

    public void logout (UserData userData) throws DataAccessException {
        AuthData auth = authDAO.getAuth(userData.username());
        if (auth == null) {
            throw new DataAccessException("User not logged in");
        }
        authDAO.deleteAuth(auth.authToken());
    }

}
