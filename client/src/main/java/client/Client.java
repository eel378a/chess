package client;
import model.GameData;
import chess.ChessGame;
import java.util.HashMap;

public abstract class Client {
    ServerFacade serverFacade;
    String authToken;
    String username;
    GameData game;
    ChessGame.TeamColor color;
    protected HashMap<Integer, GameData> gameList = new HashMap<>();

    public Client(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
    }

    public Client(Client other) {
        this.serverFacade = other.serverFacade;
        this.authToken = other.authToken;
        this.username = other.username;
        this.game = other.game;
        this.color = other.color;
    }

    abstract String eval(String line);
}
