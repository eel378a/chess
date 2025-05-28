package dataaccess;
import java.sql.Connection;
import java.sql.PreparedStatement;
import model.AuthData;
import java.util.ArrayList;
import java.sql.ResultSet;

import java.util.Collection;

public class SqlAuthDao extends TheSqlDao implements AuthDAO {
    public SqlAuthDao() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS tokens (
                `id` int NOT NULL AUTO_INCREMENT,
                `authToken` varchar(256),
                `username` varchar(256),
                PRIMARY KEY (`id`),
                UNIQUE INDEX(authToken)
            );
            """
        };
        configureDatabase(createStatements);
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