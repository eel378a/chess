package dataaccess;
import model.GameData;
import java.util.Collection;

public interface GameDAO {
    public void clearGames();
    public void addGame(GameData game);
    public Collection<GameData> listGames();
    public GameData getGame(int gameID);
}
