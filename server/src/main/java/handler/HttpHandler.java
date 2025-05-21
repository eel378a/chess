package handler;
import com.google.gson.Gson;

import dataaccess.*;
import requestsResults.RegisterRequest;
import requestsResults.RegisterResult;
import service.Service;
import service.UserService;
import spark.Request;
import spark.Response;

public class HttpHandler {
    UserDAO userDao = new MemoryUserDao();
    GameDAO gameDao = new MemoryGameDAO();
    AuthDAO authDao = new MemoryAuthDAO();

    Service service = new Service(userDao, gameDao, authDao);
    UserService userService = new UserService(userDao, gameDao, authDao);

    public Object clear(Request req, Response res) {
        service.clear();
        res.status(200);
        return "";
    }

    public Object register(Request req, Response res) {
        RegisterRequest request = new Gson().fromJson(req.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);
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