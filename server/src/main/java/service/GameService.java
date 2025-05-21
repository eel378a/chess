package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import requestsResults.CreateGameRequest;
import requestsResults.CreateGameResult;

public class GameService extends Service {
    public GameService(UserDAO users, GameDAO games, AuthDAO tokens) {
        super(users, games, tokens);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) {
        CreateGameResult result;
        if (request.gameName().isBlank()) {
            result = new CreateGameResult(null, "Error: bad request");
        } else if (isValidAuthToken(authToken)) {
            throw new RuntimeException("Not implemented");
        } else {
            result = new CreateGameResult(null, "Error: unauthorized");
        }
        return result;
    }
}