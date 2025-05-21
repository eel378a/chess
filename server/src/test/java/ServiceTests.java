import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.Service;
import service.UserService;

public class ServiceTests {

    @Test
    void clear() throws DataAccessException {
        UserDAO users = new MemoryUserDao();
        GameDAO games = new MemoryGameDAO();
        AuthDAO tokens = new MemoryAuthDAO();

        users.addUser(new UserData("isaac", "isaac", "isaac"));
        games.addGame(new GameData(1, "white", "black", "game", new ChessGame()));
        tokens.addAuthToken(new AuthData("authToken", "isaac"));

        Service service = new Service(users, games, tokens);
        service.clear();
        UserService service = new UserService(users, games, tokens);


        assert(users.listUsers().isEmpty());
        assert(games.listGames().isEmpty());
        assert(tokens.listAuthTokens().isEmpty());
    }
}