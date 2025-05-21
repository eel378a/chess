import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Service;
import service.UserService;
import requestsResults.RegisterRequest;
import requestsResults.RegisterResult;

import java.beans.beancontext.BeanContextChild;

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
    void register_success() throws DataAccessException {
//        UserDAO users = new MemoryUserDao();
//        GameDAO games = new MemoryGameDAO();
//        AuthDAO tokens = new MemoryAuthDAO();
//
//        UserService service = new UserService(users, games, tokens);
        RegisterRequest request = new RegisterRequest("jeff", "password", "email");

        RegisterResult result = service.register(request);

        assert !users.listUsers().isEmpty();
        assert !tokens.listAuthTokens().isEmpty();
        assert result.userName().equals("jeff");
    }

    @Test
    void register_400() throws DataAccessException {
        RegisterRequest request = new RegisterRequest("", "password", "email");

        RegisterResult result = service.register(request);

        assert result.message().equals("Error: bad request");
    }
}