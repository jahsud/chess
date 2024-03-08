package dataAccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;


public class UserDataAccessTests {
    private UserService userService;

    @BeforeEach
    void setup () throws DataAccessException {
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();

        this.userService = new UserService(userDAO, authDAO);

        userService.clear();
    }

    @Test
    void clearUserTest () throws DataAccessException, BadRequestException, AlreadyTakenException {
        userService.register(new RegisterRequest("user1", "password", "email"));
        userService.clear();
        assertThrows(UnauthorizedException.class, () -> userService.login(new LoginRequest("user1", "password")));
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
        assertThrows(UnauthorizedException.class, () -> userService.logout(new LogoutRequest(auth.authToken())));
    }

}
