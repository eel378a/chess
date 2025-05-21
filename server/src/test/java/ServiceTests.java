import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Service;
import service.UserService;
//import requestsResults.RegisterRequest;
//import requestsResults.RegisterResult;
//import requestsResults.LoginRequest;
//import requestsResults.LoginResult;

import requestsResults.*;


public class ServiceTests {

    UserDAO users = new MemoryUserDao();
    GameDAO games = new MemoryGameDAO();
    AuthDAO tokens = new MemoryAuthDAO();

    UserService service = new UserService(users, games, tokens);

    @BeforeEach
    void reset() {
        service.clear();
    }

    //tests
    @Test
    void clear() throws DataAccessException {
        UserDAO users = new MemoryUserDao();
        GameDAO games = new MemoryGameDAO();
        AuthDAO tokens = new MemoryAuthDAO();

        users.addUser(new UserData("jeff", "jeff", "jeff"));
        games.addGame(new GameData(1, "white", "black", "game", new ChessGame()));
        tokens.addAuthToken(new AuthData("authToken", "jeff"));

        Service service = new Service(users, games, tokens);
        service.clear();

        assert(users.listUsers().isEmpty());
        assert(games.listGames().isEmpty());
        assert(tokens.listAuthTokens().isEmpty());
    }

    @Test
    void register_200() throws DataAccessException {
//        UserDAO users = new MemoryUserDao();
//        GameDAO games = new MemoryGameDAO();
//        AuthDAO tokens = new MemoryAuthDAO();
//
//        UserService service = new UserService(users, games, tokens);
        RegisterRequest request = new RegisterRequest("jeff", "password", "email");

        RegisterResult result = service.register(request);

        assert !users.listUsers().isEmpty();
        assert !tokens.listAuthTokens().isEmpty();
        assert result.username().equals("jeff");
    }

    @Test
    void register_400() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("", "password", "email");

        RegisterResult result = service.register(request);

        assert result.message().equals("Error: bad request");
    }

    @Test
    void register_403() throws DataAccessException {
        RegisterRequest requestOne = new RegisterRequest("name", "password", "email");
        RegisterRequest requestTwo = new RegisterRequest("name", "password", "email");

        service.register(requestOne);
        RegisterResult result = service.register(requestTwo);

        assert result.message().equals("Error: already taken");
        assert users.listUsers().size() == 1;
    }

    @Test
    void login_200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("jeff", "password", "email");
        LoginRequest loginRequest = new LoginRequest("jeff", "password");

        service.register(registerRequest);
        LoginResult result = service.login(loginRequest);

        assert result.username().equals("jeff");
        assert !result.authToken().isBlank();
    }

    @Test
    void login_401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("jeff", "password", "email");
        LoginRequest loginRequest = new LoginRequest("jeff", "password1");

        service.register(registerRequest);
        LoginResult result = service.login(loginRequest);

        assert result.username() == null;
        assert result.message().equals("Error: unauthorized");
    }

    @Test
    void logout_200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("jeff", "password", "email");
        LoginRequest loginRequest = new LoginRequest("jeff", "password");

        service.register(registerRequest);
        LoginResult loginResult = service.login(loginRequest);

        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());
        EmptyResult result = service.logout(logoutRequest);

        assert result.message() == null;
        assert users.getUser(loginResult.username()) == null;
        assert tokens.getAuthData(loginResult.authToken()) == null;
    }

    @Test
    void logout_401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("jeff", "password", "email");

        RegisterResult registerResult = service.register(registerRequest);

        LogoutRequest logoutRequest = new LogoutRequest("invalid authToken");
        EmptyResult result = service.logout(logoutRequest);

        assert result.message().equals("Error: unauthorized");
    }
}