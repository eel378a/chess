package service;

import java.util.UUID;

import dataaccess.DataAccessException;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;

import javax.xml.crypto.Data;

public class Service {
    protected UserDAO users;
    protected GameDAO games;
    protected AuthDAO tokens;

    public Service(UserDAO users, GameDAO games, AuthDAO tokens) {
        this.users = users;
        this.games = games;
        this.tokens = tokens;
    }

    public void clear() throws DataAccessException {
        users.clearUsers();
        games.clearGames();
        tokens.clearAuthTokens();    }

    protected boolean isValidAuthToken(String authToken) {
        AuthData result = tokens.getAuthData(authToken);
        return result != null;
    }

    protected String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}