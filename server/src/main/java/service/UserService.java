package service;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import model.UserData;

public class UserService {

    private final DataAccess dataAccess;

    public UserService (DataAccess dataAccess) throws DataAccessException {
        this.dataAccess = dataAccess;
    }

    public UserData createUser (String username, String password, String email) throws DataAccessException {
        return dataAccess.createUser(username, password, email);
    }

    public UserData getUser (String username) throws DataAccessException {
        return dataAccess.getUser(username);
    }

}
