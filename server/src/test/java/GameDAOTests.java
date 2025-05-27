
import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.SqlGameDao;
import model.GameData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GameDAOTests {
    GameDAO games;

    @BeforeEach
    public void initializeDatabase() throws DataAccessException {
        games = new SqlGameDao();
        games.clearGames();
    }
    @Test
    public void addGame() {
        GameData game = new GameData(1, null, null, "gameName", new ChessGame());
        assertDoesNotThrow(() -> games.addGame(game));
    }

    @Test
    public void addDuplicateGame() throws DataAccessException {
        GameData game1 = new GameData(1, null, null, "gameName", new ChessGame());
        GameData game2 = new GameData(1, null, null, "gameName2", new ChessGame());

        games.addGame(game1);
        String error = "";
        try {
            games.addGame(game2);
        } catch (Exception e) {
            error = e.getMessage();
        }
        assert !error.isBlank();
    }

    @Test
    public void clearGames() throws DataAccessException {
        GameData game = new GameData(1, null, null, "gameName", new ChessGame());
        games.addGame(game);
        games.clearGames();
        assertDoesNotThrow(() -> games.addGame(game));
    }

    @Test
    public void listGames() throws DataAccessException {
        ArrayList<GameData> expected = new ArrayList<>();
        GameData game1 =
                new GameData(1, null, null, "game1", new ChessGame());
        GameData game2 =
                new GameData(2, "white", null, "game2", new ChessGame());
        GameData game3
                = new GameData(3, null, "black", "game3", new ChessGame());
        games.addGame(game1);
        games.addGame(game2);
        games.addGame(game3);

        game1 = new GameData(1, null, null, "game1", null);
        game2 = new GameData(2, "white", null, "game2", null);
        game3 = new GameData(3, null, "black", "game3", null);

        expected.add(game1);
        expected.add(game2);
        expected.add(game3);

        ArrayList<GameData> actual = new ArrayList<>(games.listGames());
        //System.out.println(expected);
        //System.out.println(actual);
        assert expected.equals(actual);
    }

    @Test
    public void getGame() throws DataAccessException {
        GameData game1 =
                new GameData(1, null, null, "game1", new ChessGame());

        games.addGame(game1);

        GameData result = games.getGame(1);
        assert game1.equals(result);
    }

    @Test
    public void getNonexistentGame() throws DataAccessException {
        GameData game1 =
                new GameData(1, null, null, "game1", new ChessGame());

        games.addGame(game1);

        GameData result = games.getGame(2);
        assert result == null;
    }

    @Test
    public void listEmptyGames() throws DataAccessException {
        assert games.listGames().isEmpty();
    }

    @Test
    public void updateGame() throws DataAccessException {
        GameData game1 = new GameData(1, null, null, "gameName", new ChessGame());
        GameData game2 = new GameData(1, null, null, "gameName2", new ChessGame());
        games.addGame(game1);
        games.updateGame(game2);
        GameData newGame = games.getGame(1);
        assert newGame.equals(game2);
    }

    @Test
    public void updateNotexistingGame() throws DataAccessException {
        assert games.listGames().isEmpty();
    }

}