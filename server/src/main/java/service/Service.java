package service;

import java.util.UUID;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import requestsResults.RegisterRequest;
import requestsResults.RegisterResult;

public class Service {
    protected UserDAO users;
    protected GameDAO games;
    protected AuthDAO tokens;

    public Service(UserDAO users, GameDAO games, AuthDAO tokens) {
        this.users = users;
        this.games = games;
        this.tokens = tokens;
    }

    public void clear() {
        users.clearUsers();
        games.clearGames();
        tokens.clearAuthTokens();    }

    protected boolean isValidAuthToken(AuthData authData) {
        return tokens.getAuthData(authData.authToken()).username().equals(authData.username());
    }

    protected String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}