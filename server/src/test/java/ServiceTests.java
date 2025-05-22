import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Service;
import service.UserService;
import service.GameService;

import requestsresults.*;


public class ServiceTests {

    UserDAO users = new MemoryUserDao();
    GameDAO games = new MemoryGameDAO();
    AuthDAO tokens = new MemoryAuthDAO();

    UserService userService = new UserService(users, games, tokens);
    GameService gameService = new GameService(users, games, tokens);

    @BeforeEach
    void reset() {
        userService.clear();
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

    //register tests
    @Test
    void register200() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("jeff", "password", "email");

        RegisterResult result = userService.register(request);

        assert !users.listUsers().isEmpty();
        assert !tokens.listAuthTokens().isEmpty();
        assert result.username().equals("jeff");
    }
    @Test
    void register400() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("", "password", "email");

        RegisterResult result = userService.register(request);

        assert result.message().equals("Error: bad request");
    }
    @Test
    void register403() throws DataAccessException {
        RegisterRequest requestOne = new RegisterRequest("name", "password", "email");
        RegisterRequest requestTwo = new RegisterRequest("name", "password", "email");

        userService.register(requestOne);
        RegisterResult result = userService.register(requestTwo);

        assert result.message().equals("Error: already taken");
        assert users.listUsers().size() == 1;
    }

    //login tests
    @Test
    void login200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("jeff", "password", "email");
        LoginRequest loginRequest = new LoginRequest("jeff", "password");

        userService.register(registerRequest);
        LoginResult result = userService.login(loginRequest);

        assert result.username().equals("jeff");
        assert !result.authToken().isBlank();
    }
    @Test
    void login401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("jeff", "password", "email");
        LoginRequest loginRequest = new LoginRequest("jeff", "password2");

        userService.register(registerRequest);
        LoginResult result = userService.login(loginRequest);

        assert result.username() == null;
        assert result.message().equals("Error: unauthorized");
    }

    //logout tests
    @Test
    void logout200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("jeff", "secret", "email");
        LoginRequest loginRequest = new LoginRequest("jeff", "secret");

        RegisterResult registerResult = userService.register(registerRequest);

        LogoutRequest logoutRequest = new LogoutRequest(registerResult.authToken());
        EmptyResult result = userService.logout(logoutRequest);

        assert result.message() == null;
        assert users.getUser(registerResult.username()) == null;
        assert tokens.getAuthData(registerResult.authToken()) == null;
    }
    @Test
    void logout401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("jeff", "password", "email");
        userService.register(registerRequest);

        LogoutRequest logoutRequest = new LogoutRequest("invalid authToken");
        EmptyResult result = userService.logout(logoutRequest);

        assert result.message().equals("Error: unauthorized");
    }

    //create games tests
    @Test
    void createGame200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("name", "password", "email");

        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("new game");
        CreateGameResult result = gameService.createGame(createGameRequest, registerResult.authToken());

        assert result.message() == null;
        assert result.gameID() != null;
        assert games.listGames().size() == 1;
    }
    @Test
    void createGame401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("name", "password", "email");
        userService.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("new game");
        CreateGameResult result = gameService.createGame(createGameRequest, "invalid authToken");

        assert result.message().equals("Error: unauthorized");
    }

    //list games tests
    @Test
    void listGames200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("jeff", "password", "email");

        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("new game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, registerResult.authToken());

        ListGamesRequest listGamesRequest = new ListGamesRequest(registerResult.authToken());
        ListGamesResult result = gameService.listGame(listGamesRequest);

        assert result.message() == null;
        assert result.games().size() == 1;
    }
    @Test
    void listGames401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("geoffery", "password", "email");
        userService.register(registerRequest);

        ListGamesRequest listGamesRequest = new ListGamesRequest("invalid authToken");
        ListGamesResult result = gameService.listGame(listGamesRequest);

        assert result.message().equals("Error: unauthorized");
    }


//join game tests

    @Test
    void joinGame200() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("billy", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("this Game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, registerResult.authToken());

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID());
        EmptyResult result = gameService.joinGame(joinGameRequest, registerResult.authToken());

        assert result.message() == null;
        assert games.getGame(createGameResult.gameID()).whiteUsername().equals("billy");
    }
    @Test
    void joinGame401() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("sally", "password", "email");
        userService.register(registerRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest("BLACK", 34);
        EmptyResult result = gameService.joinGame(joinGameRequest, "invalid authToken");

        assert result.message().equals("Error: unauthorized");
    }
    @Test
    void joinGame400badGame() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("silly", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 72);
        EmptyResult result = gameService.joinGame(joinGameRequest, registerResult.authToken());

        assert result.message().equals("Error: bad request");
    }
    @Test
    void joinGame400badColor() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("susie", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);

        CreateGameRequest createGameRequest = new CreateGameRequest("new game");
        CreateGameResult createGameResult = gameService.createGame(createGameRequest, registerResult.authToken());

        JoinGameRequest joinGameRequest = new JoinGameRequest("PURPLE", createGameResult.gameID());
        EmptyResult result = gameService.joinGame(joinGameRequest, registerResult.authToken());

        assert result.message().equals("Error: bad request");
    }
    @Test
    void joinGame403() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("susie", "password", "email");
        RegisterResult registerResult = userService.register(registerRequest);

        games.addGame(new GameData(0, "jack", "jill", "a game", new ChessGame()));

        JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", 0);
        EmptyResult result = gameService.joinGame(joinGameRequest, registerResult.authToken());

        assert result.message().equals("Error: already taken");
    }

}