package dataaccess;
import model.GameData;
import java.util.HashMap;
import java.util.Collection;

public class MemoryGameDAO implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void clearGames() {
        games.clear();
    }

    @Override
    public void addGame(GameData game) {
        games.put(game.gameID(), game);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }
}