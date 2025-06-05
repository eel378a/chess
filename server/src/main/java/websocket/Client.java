package websocket;

import org.eclipse.jetty.websocket.api.Session;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.Objects;

import chess.ChessGame;
import websocket.messages.ServerMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.LoadSMessage;

public class Client {
    public String username;
    public Session session;

    public Client(String username, Session session) {
        this.username = username;
        this.session = session;
    }

    public void sendLoadGame(ChessGame game) throws IOException {
        LoadSMessage message = new LoadSMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
        session.getRemote().sendString(new Gson().toJson(message));
    }

    public void sendNotification(String message) throws IOException {
        NotificationMessage serverMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        session.getRemote().sendString(new Gson().toJson(serverMessage));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Client client = (Client) o;
        return Objects.equals(username, client.username) && Objects.equals(session, client.session);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, session);
    }
}