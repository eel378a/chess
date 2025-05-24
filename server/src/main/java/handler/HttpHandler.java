package handler;
import com.google.gson.Gson;

import dataaccess.*;
import requestsresults.LoginRequest;
import requestsresults.LoginResult;
import requestsresults.RegisterRequest;
import requestsresults.RegisterResult;
import requestsresults.EmptyResult;
import requestsresults.LogoutRequest;
import requestsresults.CreateGameRequest;
import requestsresults.CreateGameResult;
import requestsresults.ListGamesResult;
import requestsresults.ListGamesRequest;
import requestsresults.JoinGameRequest;
import service.Service;
import service.UserService;
import service.GameService;
import spark.Request;
import spark.Response;

public class HttpHandler {
    UserDAO userDao = new MemoryUserDao();
    GameDAO gameDao = new MemoryGameDAO();
    AuthDAO authDao = new MemoryAuthDAO();

    Service service = new Service(userDao, gameDao, authDao);
    UserService userService = new UserService(userDao, gameDao, authDao);
    GameService gameService = new GameService(userDao, gameDao, authDao);

    public Object clear(Request req, Response res) throws DataAccessException{
        service.clear();
        res.status(200);
        return "";
    }
    public Object login(Request req, Response res) {
        LoginRequest request = new Gson().fromJson(req.body(), LoginRequest.class);
        LoginResult result = userService.login(request);
        res.status(getStatusCodeFromMessage(result.message()));
        return new Gson().toJson(result);
    }

    public Object register(Request req, Response res) {
        RegisterRequest request = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);
        res.status(getStatusCodeFromMessage(result.message()));
        return new Gson().toJson(result);
    }

    public Object logout(Request req, Response res) {
        LogoutRequest request = new LogoutRequest(req.headers("authorization"));
        EmptyResult result = userService.logout(request);
        res.status(getStatusCodeFromMessage(result.message()));
        return new Gson().toJson(result);
    }

    public Object createGame(Request req, Response res) {
        CreateGameRequest request = new Gson().fromJson(req.body(), CreateGameRequest.class);
        CreateGameResult result = gameService.createGame(request, req.headers("authorization"));
        res.status(getStatusCodeFromMessage(result.message()));
        return new Gson().toJson(result);
    }

    public Object listGames(Request req, Response res) {
        ListGamesRequest request = new ListGamesRequest(req.headers("authorization"));
        ListGamesResult result = gameService.listGame(request);
        res.status(getStatusCodeFromMessage(result.message()));
        return new Gson().toJson(result);
    }

    public Object joinGame(Request req, Response res){
        JoinGameRequest request = new Gson().fromJson(req.body(), JoinGameRequest.class);
        EmptyResult result = gameService.joinGame(request, req.headers("authorization"));
        res.status(getStatusCodeFromMessage(result.message()));
        return new Gson().toJson(result);
    }

    private int getStatusCodeFromMessage(String message) {
        int status;
        if (message == null) {
            status = 200;
        } else if (message.equals("Error: unauthorized")) {
            status = 401;
        } else if (message.equals("Error: bad request")) {
            status = 400;
        } else if (message.equals("Error: already taken")) {
            status = 403;
        } else {
            status = 500;
        }
        return status;
    }


}