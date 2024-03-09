package dataAccess;

import model.AuthData;

import static java.sql.Types.NULL;

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

}
