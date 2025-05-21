package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import requestResult.RegisterRequest;
import requestResult.RegisterResult;

public class Service {
    private UserDAO users;
    private GameDAO games;
    private AuthDAO tokens;

    public Service(UserDAO users, GameDAO games, AuthDAO tokens) {
        this.users = users;
        this.games = games;
        this.tokens = tokens;
    }

    public void clear() {
        users.clearUsers();
        games.clearGames();
        tokens.clearAuthTokens();    }

    public RegisterResult register(RegisterRequest request) {
        throw new RuntimeException("Not Implemented");
    }
}