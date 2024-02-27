package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import model.UserData;

public class UserService {

    private final AuthDAO authDAO;

    public UserService (AuthDAO authDAO) throws DataAccessException {
        this.authDAO = authDAO;
    }

    public UserData register (UserData user) throws DataAccessException {
        return null;
    }


}
