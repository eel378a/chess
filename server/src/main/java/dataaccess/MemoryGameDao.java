package dataaccess;
import model.GameData;
import java.util.HashMap;

public class MemoryGameDao implements GameDAO {
    final private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public void clearGames() {
        games.clear();
    }
}