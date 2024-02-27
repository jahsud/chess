package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;

public class AuthService {
    private final AuthDAO authDAO;

    public AuthService (AuthDAO authDAO) throws DataAccessException {
        this.authDAO = authDAO;
    }

    public AuthData createAuth (String username, String password) throws DataAccessException {
        return authDAO.createAuth(username, password);
    }

    public AuthData getAuth (String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken);
    }

    public void deleteAuth (String authToken) throws DataAccessException {
        authDAO.deleteAuth(authToken);
    }
}
