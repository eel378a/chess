package client;

import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;

import chess.ChessGame;
import com.google.gson.Gson;
import websocket.messages.ErrorSMessage;
import websocket.messages.LoadSMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import javax.websocket.MessageHandler;

public class WebsocketClient {
    private Session session;

    public WebsocketClient(String url) throws Exception {
        URI uri = new URI(url.replace("http", "ws"));
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME -> load_game(new Gson().fromJson(message, LoadSMessage.class));
                    case NOTIFICATION -> notification(new Gson().fromJson(message, NotificationMessage.class));
                    case ERROR -> error(new Gson().fromJson(message, ErrorSMessage.class));
                }
            }
        });
    }

    private void load_game(LoadSMessage serverMessage) {
        throw new RuntimeException("Not implemented");
    }

    private void notification(NotificationMessage serverMessage) {
        throw new RuntimeException("Not implemented");
    }

    private void error(ErrorSMessage serverMessage) {
        throw new RuntimeException("Not implemented");
    }
}