package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


public class AuthDAOTests {
    AuthDAO tokens;

    @BeforeEach
    public void initializeDatabase() throws DataAccessException {
        tokens = new SqlAuthDao();
        tokens.clearAuthTokens();
    }

    @Test
    public void addAuthToken() {
        AuthData token = new AuthData(UUID.randomUUID().toString(), "name");
        assertDoesNotThrow(() -> tokens.addAuthToken(token));
    }

    @Test
    public void clearAuthTokens() throws DataAccessException {
        AuthData token = new AuthData(UUID.randomUUID().toString(), "name");
        tokens.addAuthToken(token);
        tokens.clearAuthTokens();
        assertDoesNotThrow(() -> tokens.addAuthToken(token));
    }

    @Test
    public void listAuthTokens() throws DataAccessException {
        ArrayList<AuthData> expected = new ArrayList<>();
        AuthData token1 = new AuthData("id1", "name1");
        AuthData token2 = new AuthData("id2", "name2");
        AuthData token3 = new AuthData("id3", "name3");
        tokens.addAuthToken(token1);
        tokens.addAuthToken(token2);
        tokens.addAuthToken(token3);
        expected.add(token1);
        expected.add(token2);
        expected.add(token3);

        ArrayList<AuthData> actual = new ArrayList<>(tokens.listAuthTokens());
        assert expected.equals(actual);
    }

    @Test
    public void getAuthToken() throws DataAccessException {
        AuthData token =
                new AuthData("authToken", "name");

        tokens.addAuthToken(token);

        AuthData result = tokens.getAuthData("authToken");
        assert token.equals(result);
    }

    @Test
    public void getNonexistentAuthToken() throws DataAccessException {
        AuthData result = tokens.getAuthData("authToken");
        assert result == null;
    }

    @Test
    public void removeAuthToken() throws DataAccessException {
        AuthData token =
                new AuthData("authToken", "name");

        tokens.addAuthToken(token);

        tokens.removeAuthData("authToken");
        assert tokens.getAuthData(token.authToken()) == null;
    }

    @Test
    public void removeNonexistentAuthToken() throws DataAccessException {
        assertDoesNotThrow(() -> tokens.removeAuthData("authToken"));
    }

    @Test
    public void listEmptyAuthTokens() throws DataAccessException {
        assert tokens.listAuthTokens().isEmpty();
    }
}
