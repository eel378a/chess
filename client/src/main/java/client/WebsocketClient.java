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
import model.GameData;
import websocket.commands.UserGameCommand;

public class WebsocketClient {
    private Session session;
    private GameClient gamePlayClient;

    public WebsocketClient(String url, GameClient gamePlayClient) throws Exception {
        this.gamePlayClient = gamePlayClient;
        URI uri = new URI(url.replace("http", "ws") + "/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                switch (serverMessage.getServerMessageType()) {
                    case LOAD_GAME -> loadGame(new Gson().fromJson(message, LoadSMessage.class));
                    case NOTIFICATION -> notification(new Gson().fromJson(message, NotificationMessage.class));
                    case ERROR -> error(new Gson().fromJson(message, ErrorSMessage.class));
                }
            }
        });
    }

    private void loadGame(LoadSMessage serverMessage) {
        GameData gameData = gamePlayClient.game;
        GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                gameData.gameName(), serverMessage.game);
        gamePlayClient.setGame(newGameData);
        gamePlayClient.printBoard();
    }

    public void sendLeave() {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, gamePlayClient.authToken,
                    gamePlayClient.game.gameID());
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception e) {
            gamePlayClient.printNotification("Error: " + e.getMessage());
        }
    }

    public void sendResign() {
        try {
            UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, gamePlayClient.authToken,
                    gamePlayClient.game.gameID());
            session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (Exception e) {
            gamePlayClient.printNotification("Error: " + e.getMessage());
        }
    }

    private void notification(NotificationMessage serverMessage) {
        throw new RuntimeException("Not implemented");
    }

    private void error(ErrorSMessage serverMessage) {
        throw new RuntimeException("Not implemented");
    }
}