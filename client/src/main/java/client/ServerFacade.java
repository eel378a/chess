package client;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;

public class ServerFacade {
    public ServerFacade(String url) {
        throw new RuntimeException("Not implemented");
    }

    public void clear() {
        throw new RuntimeException("Not implemented");
    }

    public AuthData register(UserData user) {
        throw new RuntimeException("Not implemented");
    }

    public AuthData login(String username, String password) {
        throw new RuntimeException("Not implemented");
    }

    public void logout(String authToken) {
        throw new RuntimeException("Not implemented");
    }

    public int createGame(String gameName) {
        throw new RuntimeException("Not implemented");
    }

    public Collection<GameData> listGames(String authToken) {
        throw new RuntimeException("Not implemented");
    }

    public void joinGame(String playerColor, int gameID) {
        throw new RuntimeException("Not implemented");
    }
}
