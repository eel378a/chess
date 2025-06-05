package server;
import handler.HttpHandler;
import spark.*;
import websocket.WebsocketHandler;

public class Server {

    private final WebsocketHandler websocketHandler;
    HttpHandler httpHandler;
    public Server() {
        httpHandler = new HttpHandler();
        websocketHandler= new WebsocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");
        Spark.webSocket("/ws", websocketHandler);

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", httpHandler::clear);
        Spark.post("/user", httpHandler::register);
        Spark.post("/session", httpHandler::login);
        Spark.delete("/session", httpHandler::logout);
        Spark.get("/game", httpHandler::listGames);
        Spark.post("/game", httpHandler::createGame);
        Spark.put("/game", httpHandler::joinGame);
//        //This line initializes the server and can be removed once you have a functioning endpoint
        //Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}