package service;

import java.util.ArrayList;
import java.util.Collection;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import requestsResults.CreateGameRequest;
import requestsResults.CreateGameResult;
import requestsResults.ListGamesRequest;
import requestsResults.ListGamesResult;

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

    public ListGamesResult listGame(ListGamesRequest request) {
        ListGamesResult result;
        if (isValidAuthToken(request.authToken())) {
            try {
                Collection<GameData> gameList = getGameListData(games.listGames());
                result = new ListGamesResult(gameList, null);
            } catch (Exception e) {
                result = new ListGamesResult(null, "Error: ".concat(e.getMessage()));
            }        } else {
            result = new ListGamesResult(null, "Error: unauthorized");
        }
        return result;
    }


    //takes: array of GameData objects, returns: list of game IDs, gameNames, and usernames
    private Collection<GameData> getGameListData(Collection<GameData> gameList) {
        Collection<GameData> newGameList = new ArrayList<>();
        for (GameData game : gameList) {
            newGameList.add(new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), null));
        }
        return newGameList;
    }
}