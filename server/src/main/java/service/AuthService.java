package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.AuthData;

public class AuthService {
    private final DataAccess dataAccess;

    public AuthService (DataAccess dataAccess) throws DataAccessException {
        this.dataAccess = dataAccess;
    }

    public AuthData createAuth (String username, String password) throws DataAccessException {
        return dataAccess.createAuth(username, password);
    }

    public AuthData getAuth (String authToken) throws DataAccessException {
        return dataAccess.getAuth(authToken);
    }

    public void deleteAuth (String authToken) throws DataAccessException {
        dataAccess.deleteAuth(authToken);
    }
}
