package dataaccess;
import java.util.HashMap;
import model.AuthData;

public class MemoryAuthDAO implements GameDAO {
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    @Override
    public void clearGames() {
        authTokens.clear();
    }
}
