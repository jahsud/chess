package dataAccess;

import dataAccess.exceptions.DataAccessException;
import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> authTokens = new HashMap<>();

    public void clear () {
        authTokens.clear();
    }

    public AuthData createAuth (String username) throws DataAccessException {
        if (username == null || username.isEmpty()) {
            throw new DataAccessException("Username not provided");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(authToken, username);
        authTokens.put(authToken, newAuth);
        return newAuth;
    }

    public AuthData getAuth (String authToken) throws DataAccessException {
        if (authToken == null || authToken.isEmpty()) {
            throw new DataAccessException("Auth token not provided");
        }
        return authTokens.get(authToken);
    }

    public void deleteAuth (String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("Auth token not found");
        }
        authTokens.remove(authToken);
    }
}
