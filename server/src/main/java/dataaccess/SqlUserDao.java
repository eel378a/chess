package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import static java.sql.Types.NULL;

public class SqlUserDao implements UserDAO {
    public SqlUserDao() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clearUsers() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void addUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?);";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        executeUpdate(statement, user.username(), hashedPassword, user.email());
    }

    @Override
    public Collection<UserData> listUsers() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public UserData getUser(String username) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public UserData getUserByNameAndPassword(String username, String password) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void removeUser(String username) {
        throw new RuntimeException("Not implemented");
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                `id` int NOT NULL AUTO_INCREMENT,
                `username` varchar(256),
                `password` varchar(256),
                `email` varchar(256),
                PRIMARY KEY (`id`),
                INDEX(username),
                INDEX(password),
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
}