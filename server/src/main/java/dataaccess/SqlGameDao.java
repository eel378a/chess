package dataaccess;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;


public class SqlGameDao implements GameDAO{
    public SqlGameDao() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clearGames() throws DataAccessException {
        String statement = "TRUNCATE games";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Error clearing database: ".concat(e.getMessage()));
        }
    }

    @Override
    public void addGame(GameData game) throws DataAccessException {
        String statement =
                "INSERT INTO games (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        String gameDataJson = new Gson().toJson(game.game());
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, game.gameID());
                preparedStatement.setString(2, game.whiteUsername());
                preparedStatement.setString(3, game.blackUsername());
                preparedStatement.setString(4, game.blackUsername());
                preparedStatement.setString(5, gameDataJson);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Error updating database: ".concat(e.getMessage()));
        }
    }

    @Override
    public Collection<GameData> listGames() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public GameData getGame(int gameID) {
        throw new RuntimeException("Not implemented");
    }

    private final String[] createStatements = {
            """
        CREATE TABLE IF NOT EXISTS games (
            `gameID` int NOT NULL,
            `whiteUsername` varchar(256),
            `blackUsername` varchar(256),
            `gameName` varchar(256),
            `game` TEXT,
            PRIMARY KEY (`gameID`)
        );
        """
    };

    private void configureDatabase() throws DataAccessException {
        try {
            DatabaseManager.createDatabase();
        } catch(DataAccessException e) {
            throw new RuntimeException(String.format("Error creating database: %s", e.getMessage()));
        }
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Error configuring database: ".concat(e.getMessage()));
        }
    }
}
