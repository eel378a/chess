package service;

import java.util.UUID;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;

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

    protected boolean isValidAuthToken(String authToken) {
        AuthData result = tokens.getAuthData(authToken);
        if (result == null) {
            return false;
        } else {
            return true;
        }
    }

    protected String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}