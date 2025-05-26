package dataaccess;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import model.AuthData;
import java.util.ArrayList;
import java.sql.ResultSet;

import java.util.Collection;
import java.util.List;
import static java.sql.Types.NULL;


public class SqlAuthDao implements AuthDAO {
    public SqlAuthDao() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clearAuthTokens() throws DataAccessException {
        executeUpdate("TRUNCATE tokens");
    }

    @Override
    public void addAuthToken(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO tokens (authToken, username) VALUES (?, ?);";
        executeUpdate(statement, authData.authToken(), authData.username());    }

    @Override
    public Collection<AuthData> listAuthTokens() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT authToken, username FROM tokens;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                return formatListAuthTokensResult(ps);
            }
        } catch (Exception e) {
            throw new DataAccessException("Error reading database: ".concat(e.getMessage()));
        }
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT authToken, username FROM tokens WHERE authToken=?;";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                return formatGetAuthTokenResult(ps);
            }
        } catch (Exception e) {
            throw new DataAccessException("Error reading database: ".concat(e.getMessage()));
        }
    }

    @Override
    public void removeAuthData(String authToken) throws DataAccessException {
        String statement = "DELETE FROM tokens WHERE authToken=?;";
        executeUpdate(statement, authToken);
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS tokens (
                `id` int NOT NULL AUTO_INCREMENT,
                `authToken` varchar(256),
                `username` varchar(256),
                PRIMARY KEY (`id`),
                INDEX(authToken),
                UNIQUE(username)
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

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                PreparedStatement updatedStatement = setStatementVariables(ps, params);
                updatedStatement.executeUpdate();
            }
        } catch (Exception e) {
            throw new DataAccessException("Error updating database: ".concat(e.getMessage()));
        }
    }

    private PreparedStatement setStatementVariables(PreparedStatement ps, Object ... params) throws SQLException {
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

    private ArrayList<AuthData> formatListAuthTokensResult(PreparedStatement ps) throws DataAccessException {
        ArrayList<AuthData> result = new ArrayList<>();
        try (ResultSet resultSet = ps.executeQuery()) {
            while (resultSet.next()) {
                String authToken = resultSet.getString("authToken");
                String username = resultSet.getString("username");
                result.add(new AuthData(authToken, username));
            }
            return result;
        } catch (Exception e) {
            throw new DataAccessException("Error executing query: ".concat(e.getMessage()));
        }
    }

    private AuthData formatGetAuthTokenResult(PreparedStatement ps) throws DataAccessException {
        try (ResultSet resultSet = ps.executeQuery()) {
            if (resultSet.next()) {
                String authToken = resultSet.getString("authToken");
                String username = resultSet.getString("username");
                return new AuthData(authToken, username);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new DataAccessException("Error executing query: ".concat(e.getMessage()));
        }
    }
}