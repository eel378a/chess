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
    boolean activeGame;

    public Client(ServerFacade serverFacade) {
        this.serverFacade = serverFacade;
        this.activeGame = false;
    }

    public GameData getGame(){
        return game;
    }
    public void setGame(GameData game){
        this.game = game;
    }

    public Client(Client other) {
        this.serverFacade = other.serverFacade;
        this.authToken = other.authToken;
        this.username = other.username;
        this.game = other.game;
        this.color = other.color;
        this.activeGame = other.activeGame;
    }

    abstract String eval(String line);
}
