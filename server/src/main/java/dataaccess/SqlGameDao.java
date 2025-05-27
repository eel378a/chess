package dataaccess;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.ArrayList;
import java.sql.ResultSet;
import chess.ChessGame;
import static java.sql.Types.NULL;


public class SqlGameDao implements GameDAO{
    public SqlGameDao() throws DataAccessException {
        String[] createStatements = {
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
        configureDatabase(createStatements);
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
    public void updateGame(GameData game) throws DataAccessException {
        String statement =
                "UPDATE games SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?";
        String gameDataJson = new Gson().toJson(game.game());
        executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), gameDataJson, game.gameID());
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
                preparedStatement.setString(4, game.gameName());
                preparedStatement.setString(5, gameDataJson);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Error updating database: ".concat(e.getMessage()));
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName FROM games;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                return formatListGameResult(ps);
            }
        } catch (Exception e) {
            throw new DataAccessException("Error reading database: ".concat(e.getMessage()));
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM games WHERE gameID=?;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                return formatGetGameResult(ps);
            }
        } catch (Exception e) {
            throw new DataAccessException("Error reading database: ".concat(e.getMessage()));
        }
    }

    private void configureDatabase(String[] createStatements) throws DataAccessException {
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

        private ArrayList<GameData> formatListGameResult(PreparedStatement ps) throws DataAccessException {
            ArrayList<GameData> result = new ArrayList<>();
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    int gameID = resultSet.getInt("gameID");
                    String whiteUsername = resultSet.getString("whiteUsername");
                    String blackUsername = resultSet.getString("blackUsername");
                    String gameName = resultSet.getString("gameName");
                    result.add(new GameData(gameID, whiteUsername, blackUsername, gameName, null));
                }
                return result;
            } catch (Exception e) {
                throw new DataAccessException("Error executing query: ".concat(e.getMessage()));
            }
        }

    private GameData formatGetGameResult(PreparedStatement ps) throws DataAccessException {
        try (ResultSet resultSet = ps.executeQuery()) {
            if (resultSet.next()) {
                int gameID = resultSet.getInt("gameID");
                String whiteUsername = resultSet.getString("whiteUsername");
                String blackUsername = resultSet.getString("blackUsername");
                String gameName = resultSet.getString("gameName");
                String gameJson = resultSet.getString("game");
                ChessGame game = new Gson().fromJson(gameJson, ChessGame.class);
                return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new DataAccessException("Error executing query: ".concat(e.getMessage()));
        }
    }

    protected void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                PreparedStatement updatedStatement = setStatementVariables(ps, params);
                updatedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Error updating database: ".concat(e.getMessage()));
        }
    }

    protected PreparedStatement setStatementVariables(PreparedStatement ps, Object ... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            var param = params[i];
            switch (param) {
                case Integer p -> ps.setInt(i + 1, p);
                case String p -> ps.setString(i + 1, p);
                case null -> ps.setNull(i + 1, NULL);
                default -> {
                }
            }
        }
        return ps;
    }
}
