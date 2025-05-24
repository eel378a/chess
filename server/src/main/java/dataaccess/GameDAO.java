package dataaccess;
import model.GameData;
import java.util.Collection;

public interface GameDAO {
    public void clearGames() throws DataAccessException;
    public void addGame(GameData game) throws DataAccessException;
    public Collection<GameData> listGames() throws DataAccessException;
    public GameData getGame(int gameID) throws DataAccessException;
}
