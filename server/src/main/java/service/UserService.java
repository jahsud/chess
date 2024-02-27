package service;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;

public class UserService {
    private final MemoryUserDAO userDAO;
    private final MemoryAuthDAO authDAO;

    public UserService (MemoryUserDAO userDAO, MemoryAuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register (UserData userData) throws DataAccessException {
        if (userDAO.getUser(userData.username()) != null) {
            throw new DataAccessException("Username already exists");
        }
        UserData newUser = userDAO.createUser(userData.username(), userData.password(), userData.email());
        return new AuthData(newUser.username(), newUser.email());
    }

    public AuthData login (UserData userData) throws DataAccessException {
        UserData existingUser = userDAO.getUser(userData.username());
        if (!existingUser.password().equals(userData.password())) {
            throw new DataAccessException("Invalid password");
        }
        return new AuthData(existingUser.username(), existingUser.email());
    }

    public void logout (UserData userData) throws DataAccessException {
        AuthData auth = authDAO.getAuth(userData.username());
        if (auth == null) {
            throw new DataAccessException("User not logged in");
        }
        authDAO.deleteAuth(auth.authToken());
    }

}
