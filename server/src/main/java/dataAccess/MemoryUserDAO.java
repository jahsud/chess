package dataAccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    public void clear () {
        users.clear();
    }

    public UserData createUser (String username, String password, String email) throws DataAccessException {
        if (users.containsKey(username)) {
            throw new DataAccessException("Username already exists");
        }
        UserData newUser = new UserData(username, password, email);
        users.put(username, newUser);
        return newUser;
    }

    public UserData getUser (String username) {
        return users.get(username);
    }

    public boolean verifyPassword (String username, String password) {
        return users.get(username).password().equals(password);
    }

}

