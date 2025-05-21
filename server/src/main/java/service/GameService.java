package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import requestsResults.CreateGameRequest;
import requestsResults.CreateGameResult;

import chess.ChessGame;
import model.GameData;

public class GameService extends Service {
    private int newGameID = 0;
    public GameService(UserDAO users, GameDAO games, AuthDAO tokens) {
        super(users, games, tokens);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) {
        CreateGameResult result;
        if (request.gameName().isBlank()) {
            result = new CreateGameResult(null, "Error: bad request");
        } else if (isValidAuthToken(authToken)) {
            try {
                games.addGame(new GameData(newGameID, "", "", request.gameName(), new ChessGame()));
                result = new CreateGameResult(newGameID, null);
                newGameID++;
            } catch (Exception e) {
                result = new CreateGameResult(null, "Error: ".concat(e.getMessage()));
            }
        } else {
            result = new CreateGameResult(null, "Error: unauthorized");
        }
        return result;
    }
}