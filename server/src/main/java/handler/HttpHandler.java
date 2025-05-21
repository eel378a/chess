package handler;

import dataaccess.*;
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
}