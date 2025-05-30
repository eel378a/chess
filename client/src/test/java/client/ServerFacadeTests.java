package client;

import org.junit.jupiter.api.*;
import server.Server;

import model.UserData;
import Data.LoginResponse;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        serverFacade.clear();
        server.stop();
    }

    //register tests
    @Test
    public void register() {
        UserData user = new UserData("me", "classified", "email");
        LoginResponse result = serverFacade.register(user);
        assert result.username().equals("me");
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
        UserData user = new UserData("user", "secret", "email");
        serverFacade.register(user);
        LoginResponse response = serverFacade.login(user.username(), user.password());

        assert response.username().equals("user");
        assert !response.authToken().isBlank();
    }

    @Test
    public void unauthLogin() {
        UserData user1 = new UserData("user", "imnottellin", "email");
        serverFacade.register(user1);

        try {
            LoginResponse response = serverFacade.login(user1.username(), "wrong password");
        } catch (Exception e) {
            assert e.getMessage().equals("Error: unauthorized");
        }
    }
}
