package service;

import java.util.ArrayList;
import java.util.Collection;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
//import requestsResults.CreateGameRequest;
//import requestsResults.CreateGameResult;
//import requestsResults.ListGamesRequest;
//import requestsResults.ListGamesResult;
import requestsResults.*;

import chess.ChessGame;
import model.GameData;

public class GameService extends Service {
    private int newGameID = 1;
    public GameService(UserDAO users, GameDAO games, AuthDAO tokens) {
        super(users, games, tokens);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) {
        CreateGameResult result;
        if (request.gameName().isBlank()) {
            result = new CreateGameResult(null, "Error: bad request");
        } else if (isValidAuthToken(authToken)) {
            try {
                games.addGame(new GameData(newGameID, null, null, request.gameName(), new ChessGame()));
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

    //join game
    public EmptyResult joinGame(JoinGameRequest request, String authToken) {
        EmptyResult result;
        if (!isValidAuthToken(authToken)) {
            result = new EmptyResult("Error: unauthorized");
        } else if (!isValidColor(request.playerColor()) || !isValidGameID(request.gameID())) {
            result = new EmptyResult("Error: bad request");
        } else if (!playerColorAvailable(request)) {
            result = new EmptyResult("Error: already taken");
        } else {
            result = joinGameWithValidRequest(request, authToken);
        }
        return result;
    }

    //valid entries, player colors and game ID
    private boolean isValidColor(String playerColor) {
        return (playerColor != null) && (playerColor.equals("WHITE") || playerColor.equals("BLACK"));
    }
    private boolean isValidGameID(int gameID) {
        return games.getGame(gameID) != null;
    }
    private boolean playerColorAvailable(JoinGameRequest request) {
        GameData gameData = games.getGame(request.gameID());
        if (request.playerColor().equals("WHITE")) {
            return gameData.whiteUsername()==null;
        } else {
            return gameData.blackUsername()==null;
        }
    }
    private EmptyResult joinGameWithValidRequest(JoinGameRequest request, String authToken) {
        EmptyResult result;
        try {
            GameData gameData = games.getGame(request.gameID());
            String userName = tokens.getAuthData(authToken).username();
            if (request.playerColor().equals("WHITE")) {
                games.addGame(new GameData(gameData.gameID(), userName, gameData.blackUsername(),
                        gameData.gameName(), gameData.game()));
            } else {
                games.addGame(new GameData(gameData.gameID(), gameData.whiteUsername(), userName,
                        gameData.gameName(), gameData.game()));
            }
            result = new EmptyResult(null);
        } catch (Exception e) {
//                result = new CreateGameResult(null, "Error: ".concat(e.getMessage()));
            result = new EmptyResult("Error: ".concat(e.getMessage()));
        }
        return result;
    }
}
