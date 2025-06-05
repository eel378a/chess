package websocket;

import dataaccess.AuthDAO;
import dataaccess.SqlAuthDao;
import dataaccess.DataAccessException;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;

@WebSocket
public class WebsocketHandler {
    AuthDAO authDAO = new SqlAuthDao();
    ManageClient clients = new ManageClient();

    public WebsocketHandler() throws DataAccessException{
        //later?
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command, session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MoveCommand.class));
            case LEAVE -> leave(command);
            case RESIGN -> resign(command);
        }
    }

    private void connect(UserGameCommand command, Session session) {
        try {
            String username = getUserNameByCommand(command);
            clients.add(command.getGameID(), username, session);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
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
}