package dataAccessTests;

import dataAccess.DataAccessException;
import dataAccess.MySqlUserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDataAccessTests {

    private MySqlUserDAO userDAO;

    @BeforeEach
    void setup () throws DataAccessException {
        userDAO = new MySqlUserDAO();
        userDAO.clear();
    }

    @Test
    void positiveTestCreateUser () throws DataAccessException {
        userDAO.createUser("username", "password", "email");
        var user = userDAO.getUser("username");
        assertNotNull(user);
        assertEquals("username", user.username());
        assertTrue(userDAO.verifyPassword("username", "password"));
        assertEquals("email", user.email());
    }

    @Test
    void negativeTestCreateUser () throws DataAccessException {
        assertThrows(IllegalArgumentException.class, () -> userDAO.createUser(null, null, null));
    }

    @Test
    void positiveTestGetUser () throws DataAccessException {
        userDAO.createUser("username", "password", "email");
        var user = userDAO.getUser("username");
        assertNotNull(user);
        assertEquals("username", user.username());
        assertTrue(userDAO.verifyPassword("username", "password"));
    }

    @Test
    void negativeTestGetUser () throws DataAccessException {
        var user = userDAO.getUser("username");
        assertNull(user);
    }

    @Test
    void positiveTestVerifyPassword () throws DataAccessException {
        userDAO.createUser("username", "password", "email");
        assertTrue(userDAO.verifyPassword("username", "password"));
    }

    @Test
    void negativeTestVerifyPassword () throws DataAccessException {
        userDAO.createUser("username", "password", "email");
        assertFalse(userDAO.verifyPassword("username", "wrongPassword"));
    }
}
