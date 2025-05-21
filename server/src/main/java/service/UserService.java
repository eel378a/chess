package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import requestResult.RegisterRequest;
import requestResult.RegisterResult;

public class UserService extends Service {
    public UserService(UserDAO users, GameDAO games, AuthDAO tokens) {
        super(users, games, tokens);
    }

    public RegisterResult register(RegisterRequest request) {
        throw new RuntimeException("Not Implemented");
    }
}