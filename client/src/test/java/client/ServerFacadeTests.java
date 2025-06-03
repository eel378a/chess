package client;

import org.junit.jupiter.api.*;
import java.util.Collection;
import java.util.ArrayList;
import server.Server;
import java.util.UUID;

import model.UserData;
import model.GameData;
import data.LoginResponse;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
        //serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        serverFacade.clear();
        server.stop();
    }

    @BeforeEach
    public void clearDatabases() {
        serverFacade.clear();
    }

    //register tests
    @Test
    public void register() {
        String username = UUID.randomUUID().toString();
        UserData user = new UserData(username, "password", "email");        LoginResponse result = serverFacade.register(user);
        assert result.username().equals(username);
        assert !result.authToken().isBlank();
    }

    @Test
    public void registerDuplicate() {
        UserData user = new UserData("me", "classified", "email");
        UserData user2 = new UserData("me", "classified", "email");
        serverFacade.register(user);
        try {
            LoginResponse result = serverFacade.register(user2);
        } catch (Exception e) {
            assert e.getMessage().equals("Error: already taken");
        }
    }
    @Test
    public void registerBadRequest() {
        UserData user = new UserData("you", "secret", "");
        try {
            LoginResponse result = serverFacade.register(user);
        } catch (Exception e) {
            assert e.getMessage().equals("Error: bad request");
        }
    }

    @Test
    public void login() {
        String username = UUID.randomUUID().toString();
        UserData user = new UserData(username, "secrettt", "email");        serverFacade.register(user);
        LoginResponse response = serverFacade.login(user.username(), user.password());

        assert response.username().equals(username);
        assert !response.authToken().isBlank();
    }

    @Test
    public void unauthLogin() {
        String username = UUID.randomUUID().toString();
        UserData user = new UserData(username, "imnottellin", "email");
        serverFacade.register(user);
    }

    //logout tests
    @Test
    public void logout() {
        String username = UUID.randomUUID().toString();
        UserData user = new UserData(username, "classified", "email");
        LoginResponse response = serverFacade.register(user);
        serverFacade.logout(response.authToken());

        String result = "";
        try {
            serverFacade.createGame("game", "");
        } catch (Exception e) {
            result = e.getMessage();
        }
        assert result.equals("Error: unauthorized");
    }

    @Test
    public void unauthorizedLogout() {
        String result = "";
        try {
            serverFacade.logout("");
        } catch (Exception e) {
            result = e.getMessage();
        }
        assert result.equals("Error: unauthorized");
    }

    //create game tests
    @Test
    public void createaGame() {
        UserData user = new UserData("meee", "nottellin", "mail");
        String gameName = "game";

        LoginResponse loginResponse = serverFacade.register(user);
        int response = serverFacade.createGame(gameName, loginResponse.authToken());
        assert response > 0;
    }

    @Test
    public void unauthorizedCreateGame() {
        String result = "";
        try {
            serverFacade.createGame("game", "");
        } catch (Exception e) {
            result = e.getMessage();
        }
        assert result.equals("Error: unauthorized");
    }

    //listgames tests
    @Test
    public void listGames() {
        String username = UUID.randomUUID().toString();
        UserData user = new UserData(username, "supersecret", "email");
        String gameName = UUID.randomUUID().toString();

        LoginResponse loginResponse = serverFacade.register(user);
        serverFacade.createGame(gameName, loginResponse.authToken());

        Collection<GameData> response = serverFacade.listGames(loginResponse.authToken());
        assert response.size() == 1;
    }

    @Test
    public void unauthListGames() {
        String result = "";
        try {
            serverFacade.listGames("");
        } catch (Exception e) {
            result = e.getMessage();
        }
        assert result.equals("Error: unauthorized");
    }

    @Test
    public void joinGame() {
        String username = UUID.randomUUID().toString();
        String username2 = UUID.randomUUID().toString();
        UserData user = new UserData(username, "you", "email");
        UserData user2 = new UserData(username2, "me", "email2");
        String gameName = UUID.randomUUID().toString();

        LoginResponse loginResponse = serverFacade.register(user);
        LoginResponse loginResponse2 = serverFacade.register(user2);
        int gameID = serverFacade.createGame(gameName, loginResponse.authToken());
        serverFacade.joinGame("WHITE", gameID, loginResponse.authToken());
        serverFacade.joinGame("BLACK", gameID, loginResponse2.authToken());

        ArrayList<GameData> list = new ArrayList<>(serverFacade.listGames(loginResponse.authToken()));
        assert list.getFirst().whiteUsername().equals(loginResponse.username());
        assert list.getFirst().blackUsername().equals(loginResponse2.username());
    }

    @Test
    public void joinAlreadyTakenGame() {
        String username = UUID.randomUUID().toString();
        String username2 = UUID.randomUUID().toString();
        UserData user = new UserData(username, "me", "email");
        UserData user2 = new UserData(username2, "you", "idk");
        String gameName = UUID.randomUUID().toString();

        LoginResponse loginResponse = serverFacade.register(user);
        LoginResponse loginResponse2 = serverFacade.register(user2);
        int gameID = serverFacade.createGame(gameName, loginResponse.authToken());
        serverFacade.joinGame("WHITE", gameID, loginResponse.authToken());

        String result = "";
        try {
            serverFacade.joinGame("WHITE", gameID, loginResponse2.authToken());
        } catch (Exception e) {
            result = e.getMessage();
        }
        assert result.equals("Error: already taken");
    }

    @Test
    public void joinGameWithBadRequest() {
        String username = UUID.randomUUID().toString();
        String username2 = UUID.randomUUID().toString();
        UserData user = new UserData(username, "me", "email");
        UserData user2 = new UserData(username2, "you", "idk");
        String gameName = UUID.randomUUID().toString();

        LoginResponse loginResponse = serverFacade.register(user);
        LoginResponse loginResponse2 = serverFacade.register(user2);
        int gameID = serverFacade.createGame(gameName, loginResponse.authToken());
        serverFacade.joinGame("WHITE", gameID, loginResponse.authToken());

        String result = "";
        try {
            serverFacade.joinGame("", gameID, loginResponse2.authToken());
        } catch (Exception e) {
            result = e.getMessage();
        }
        assert result.equals("Error: bad request");
    }

    @Test
    public void unauthJoinGame() {
        String result = "";
        try {
            serverFacade.joinGame("WHITE", 1, "");
        } catch (Exception e) {
            result = e.getMessage();
        }
        assert result.equals("Error: unauthorized");
    }
}
