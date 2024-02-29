package passoffTests.serviceTests;

import dataAccess.MemoryAuthDAO;
import dataAccess.MemoryUserDAO;
import dataAccess.exceptions.AlreadyTakenException;
import dataAccess.exceptions.BadRequestException;
import dataAccess.exceptions.DataAccessException;
import dataAccess.exceptions.UnauthorizedException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.RegisterRequest;
import request.LogoutRequest;
import result.LoginResult;
import result.RegisterResult;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;


public class UserTests {
    static final UserService userService = new UserService(new MemoryUserDAO(), new MemoryAuthDAO());

    @BeforeEach
    void clear () throws DataAccessException {
        userService.clear();
    }

    @Test
    void clearUserTest () throws DataAccessException, BadRequestException, AlreadyTakenException {
        userService.register(new RegisterRequest("user1", "password", "email"));
        userService.clear();
        assertThrows(BadRequestException.class, () -> userService.login(new LoginRequest("user1", "password")));
    }

    @Test
    void registerPositiveTest () throws DataAccessException, BadRequestException, AlreadyTakenException {
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "email");
        RegisterResult user = userService.register(registerRequest);
        assertNotNull(user);
    }

    @Test
    void registerNegativeTest () throws DataAccessException, BadRequestException, AlreadyTakenException {
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "email");
        userService.register(registerRequest);
        assertThrows(AlreadyTakenException.class, () -> userService.register(registerRequest));
    }

    @Test
    void loginPositiveTest () throws DataAccessException, BadRequestException, AlreadyTakenException, UnauthorizedException {
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "email");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("user1", "password");
        LoginResult user = userService.login(loginRequest);
        assertNotNull(user);
    }

    @Test
    void loginNegativeTest () throws DataAccessException, BadRequestException, AlreadyTakenException, UnauthorizedException {
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "email");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("user1", "password");
        userService.login(loginRequest);
        assertThrows(UnauthorizedException.class, () -> userService.login(new LoginRequest("user1", "wrongpassword")));
    }

    @Test
    void logoutPositiveTest () throws DataAccessException, BadRequestException, AlreadyTakenException, UnauthorizedException {
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "email");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("user1", "password");
        LoginResult user = userService.login(loginRequest);
        AuthData auth = new AuthData(user.authToken(), user.username());
        assertDoesNotThrow(() -> userService.logout(new LogoutRequest(auth.authToken())));
    }

    @Test
    void logoutNegativeTest () throws DataAccessException, BadRequestException, AlreadyTakenException, UnauthorizedException {
        RegisterRequest registerRequest = new RegisterRequest("user1", "password", "email");
        userService.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("user1", "password");
        LoginResult user = userService.login(loginRequest);
        AuthData auth = new AuthData(user.authToken(), user.username());
        userService.logout(new LogoutRequest(auth.authToken()));
        assertThrows(DataAccessException.class, () -> userService.logout(new LogoutRequest(auth.authToken())));
    }

}
