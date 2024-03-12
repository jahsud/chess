package dataAccessTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.MySqlAuthDAO;
import model.AuthData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDataAccessTests {

    private AuthDAO authDAO;

    @BeforeEach
    void setup () throws DataAccessException {
        authDAO = new MySqlAuthDAO();
        authDAO.clear();
    }

    @Test
    void positiveTestCreateAuth () throws DataAccessException {
        AuthData auth = authDAO.createAuth("username");
        assertNotNull(auth);
        assertEquals("username", auth.username());
        assertNotNull(auth.authToken());
        assertNotNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    void negativeTestCreateAuth () throws DataAccessException {
        AuthData auth = authDAO.createAuth("username");
        assertNotNull(auth);
        assertEquals("username", auth.username());
        assertNotNull(auth.authToken());
        assertNotNull(authDAO.getAuth(auth.authToken()));
        assertDoesNotThrow(() -> authDAO.createAuth("username"));
    }

    @Test
    void positiveTestGetAuth () throws DataAccessException {
        AuthData auth = authDAO.createAuth("username");
        assertNotNull(auth);
        assertEquals("username", auth.username());
        assertNotNull(auth.authToken());
        assertNotNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    void negativeTestGetAuth () throws DataAccessException {
        assertNull(authDAO.getAuth("authToken"));
    }

    @Test
    void positiveTestDeleteAuth () throws DataAccessException {
        AuthData auth = authDAO.createAuth("username");
        assertNotNull(auth);
        assertEquals("username", auth.username());
        assertNotNull(auth.authToken());
        assertNotNull(authDAO.getAuth(auth.authToken()));
        authDAO.deleteAuth(auth.authToken());
        assertNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    void negativeTestDeleteAuth () throws DataAccessException {
        assertDoesNotThrow(() -> authDAO.deleteAuth("authToken"));
    }

    @Test
    void testClear () throws DataAccessException {
        AuthData auth = authDAO.createAuth("username");
        assertNotNull(auth);
        assertEquals("username", auth.username());
        assertNotNull(auth.authToken());
        assertNotNull(authDAO.getAuth(auth.authToken()));
        authDAO.clear();
        assertNull(authDAO.getAuth(auth.authToken()));
    }
}
