package dataaccess;
import model.GameData;

import java.util.Collection;
import java.util.List;


public class SqlGameDao implements GameDAO{
    @Override
    public void clearGames() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void addGame(GameData game) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Collection<GameData> listGames() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public GameData getGame(int gameID) {
        throw new RuntimeException("Not implemented");
    }
}
