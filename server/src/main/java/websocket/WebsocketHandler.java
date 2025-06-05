package websocket;

import dataaccess.*;
import model.GameData;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorSMessage;
import websocket.messages.ServerMessage;
import java.io.IOException;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;

@WebSocket
public class WebsocketHandler {
    AuthDAO authDAO;
    GameDAO gameDAO;
    ManageClient clients;

    public WebsocketHandler() {
        try {
            authDAO = new SqlAuthDao();
            gameDAO = new SqlGameDao();
            clients = new ManageClient();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        try {
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, session);
                case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MoveCommand.class));
                case LEAVE -> leave(command);
                case RESIGN -> resign(command);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void connect(UserGameCommand command, Session session) throws IOException {
        try {
            String username = getUserNameByCommand(command);
            Client client = new Client(username, session);
            clients.add(command.getGameID(), client);
            GameData gameData = gameDAO.getGame(command.getGameID());
            client.sendLoadGame(gameData.game());
            String playerStatus = getPlayerStatusByCommand(command, username);
            clients.notifyOtherClients(command.getGameID(), client, username + " has joined the game as " + playerStatus + ".");
        } catch (DataAccessException e) {
            ErrorSMessage error = new ErrorSMessage(ServerMessage.ServerMessageType.ERROR, "Error: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(error));
        }
    }

    private void makeMove(MoveCommand command) {
        throw new RuntimeException("Not implemented");
    }

    private void leave(UserGameCommand command) {
        throw new RuntimeException("Not implemented");
    }

    private void resign(UserGameCommand command) {
        throw new RuntimeException("Not implemented");
    }

    //helpers
    private String getUserNameByCommand(UserGameCommand command) throws DataAccessException {
        String authToken = command.getAuthToken();
        return authDAO.getAuthData(authToken).username();
    }

    private String getPlayerStatusByCommand(UserGameCommand command, String username) throws DataAccessException {
        GameData gameData = gameDAO.getGame(command.getGameID());
        if (gameData.blackUsername().equals(username)) {
            return "black";
        } else if (gameData.whiteUsername().equals(username)) {
            return "white";
        } else {
            return "an observer";
        }
    }
}