package dataAccess;

import model.AuthData;

import java.util.HashMap;

public class AuthDAO implements DataAccess {
    private final HashMap<String, AuthData> authTokens = new HashMap<>();

    public void clear () {
        authTokens.clear();
    }

    public AuthData createAuth (String authToken, String username) throws DataAccessException {
        if (authTokens.containsKey(authToken)) {
            throw new DataAccessException("Auth token already exists");
        }
        AuthData newAuth = new AuthData(authToken, username);
        authTokens.put(authToken, newAuth);
        return newAuth;
    }

    public AuthData getAuth (String authToken) throws DataAccessException {
        AuthData auth = authTokens.get(authToken);
        if (auth == null) {
            throw new DataAccessException("Auth token not found");
        }
        return auth;
    }

    public void deleteAuth (String authToken) throws DataAccessException {
        if (!authTokens.containsKey(authToken)) {
            throw new DataAccessException("Auth token not found");
        }
        authTokens.remove(authToken);
    }
}
