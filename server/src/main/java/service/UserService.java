package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import requestsResults.*;
import model.AuthData;
import model.UserData;

public class UserService extends Service {
    public UserService(UserDAO users, GameDAO games, AuthDAO tokens) {
        super(users, games, tokens);
    }

    public RegisterResult register(RegisterRequest request) {
        RegisterResult result;
        if (!isValidRequest(request)) {
            result = new RegisterResult(null, null, "Error: bad request");
        }else if(!isUniqueUsername(request.username())) {
            result = new RegisterResult(null, null, "Error: already taken");
        }else {
            try {
                users.addUser(new UserData(request.username(), request.password(), request.email()));
                AuthData authToken = new AuthData(generateAuthToken(), request.username());
                tokens.addAuthToken(authToken);
                result = new RegisterResult(authToken.username(), authToken.authToken(), null);
            } catch (Exception e) {
                result = new RegisterResult(null, null, "Error: ".concat(e.getMessage()));
            }
        }
        return result;
    }

    private boolean isValidRequest(RegisterRequest request) {
        return !request.username().isBlank() && !request.password().isBlank() && !request.email().isBlank();
    }

    private boolean isUniqueUsername(String userName) {
        return users.getUser(userName) == null;
    }

    public LoginResult login(LoginRequest request) {
        LoginResult result;
        try {
            if (users.getUserByNameAndPassword(request.username(), request.password()) != null) {
                String authToken = generateAuthToken();
                tokens.addAuthToken(new AuthData(authToken, request.username()));
                result = new LoginResult(request.username(), authToken, null);
            } else {
                result = new LoginResult(null, null, "Error: unauthorized");
            }
        } catch (Exception e) {
            result = new LoginResult(null, null, "Error: ".concat(e.getMessage()));
        }
        return result;
    }

    public EmptyResult logout(LogoutRequest request) {
        EmptyResult result;
        if (isValidAuthToken(request.authToken())) {
            try {
                AuthData authData = tokens.getAuthData(request.authToken());
                tokens.removeAuthData(authData.authToken());
                users.removeUser(authData.username());
                result = new EmptyResult(null);
            } catch (Exception e) {
                result = new EmptyResult("Error: ". concat(e.getMessage()));
            }
        } else {
            result = new EmptyResult("Error: unauthorized");
        }
        return new EmptyResult(null);
    }
}