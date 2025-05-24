
import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.SqlGameDao;
import model.GameData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class GameDAOTests {
    GameDAO games;

    @BeforeEach
    public void initializeDatabase() throws DataAccessException {
        games = new SqlGameDao();
    }
    @Test
    public void addGame() {
        GameData game = new GameData(1, null, null, "gameName", new ChessGame());
        assertDoesNotThrow(() -> games.addGame(game));
    }
}