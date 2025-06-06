package service;

import java.util.ArrayList;
import java.util.Collection;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

import requestsresults.*;
import chess.ChessGame;
import model.GameData;
import dataaccess.DataAccessException;

public class GameService extends Service {
    private int newGameID = 1;
    public GameService(UserDAO users, GameDAO games, AuthDAO tokens) {
        super(users, games, tokens);
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) {
        CreateGameResult result;
        try {
            if (request.gameName() == null) {//fixing create bad request test
                result = new CreateGameResult(null, "Error: bad request");
            } else if (isValidAuthToken(authToken)) {
                games.addGame(new GameData(newGameID, null, null, request.gameName(), new ChessGame()));
                result = new CreateGameResult(newGameID, null);
                newGameID++;
                //moved newGameID++; to before...
            } else {
                result = new CreateGameResult(null, "Error: unauthorized");
            }
        }catch (Exception e){
                result = new CreateGameResult(null, "Error: ".concat(e.getMessage()));
            }
        return result;
    }

    public ListGamesResult listGame(ListGamesRequest request) {
        ListGamesResult result;
        try {
            if (isValidAuthToken(request.authToken())) {
                Collection<GameData> gameList = getGameListData(games.listGames());
                result = new ListGamesResult(gameList, null);
            } else {
                result = new ListGamesResult(null, "Error: unauthorized");
            }
        } catch (Exception e) {
        result = new ListGamesResult(null, "Error: ".concat(e.getMessage()));
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
        try {
            if (!isValidAuthToken(authToken)) {
                result = new EmptyResult("Error: unauthorized");
            } else if (!isValidColor(request.playerColor()) || !isValidGameID(request.gameID())) {
                result = new EmptyResult("Error: bad request");
            } else if (!playerColorAvailable(request)) {
                result = new EmptyResult("Error: already taken");
            } else {
                result = joinGameWithValidRequest(request, authToken);
            }
        } catch (Exception e) {
            result = new EmptyResult("Error: ".concat(e.getMessage()));
        }
        return result;
    }

    //valid entries, player colors and game ID
    private boolean isValidColor(String playerColor) {
        return (playerColor != null) && (playerColor.equals("WHITE") || playerColor.equals("BLACK"));
    }
    private boolean isValidGameID(int gameID) throws DataAccessException{
        return games.getGame(gameID) != null;
    }
    private boolean playerColorAvailable(JoinGameRequest request) throws DataAccessException{
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
                games.updateGame(new GameData(gameData.gameID(), userName, gameData.blackUsername(),
                        gameData.gameName(), gameData.game()));
            } else {
                games.updateGame(new GameData(gameData.gameID(), gameData.whiteUsername(), userName,
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
