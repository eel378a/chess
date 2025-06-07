package websocket;

import dataaccess.*;
import model.GameData;
import model.AuthData;
import chess.ChessGame;
import chess.InvalidMoveException;
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
            String username = getUserNameByCommand(command, session);
            Client client = new Client(username, session);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command, client);
                case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MoveCommand.class), client);
                case LEAVE -> leave(command, client);
                case RESIGN -> resign(command, client);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void connect(UserGameCommand command, Client client) throws IOException {
        try {
            clients.add(command.getGameID(), client);
            sendLoadGame(client, command.getGameID());
            String playerStatus = getPlayerStatusByCommand(command, client.username);
            clients.notifyOtherClients(command.getGameID(), client, client.username + " has joined the game as " + playerStatus + ".");
        } catch (Exception e) {//error catch exception
            client.sendError("Error: " + e.getMessage());
        }
    }

    private void makeMove(MoveCommand command, Client client) throws IOException {
        try {
            canMakeMove(command, client);
            GameData gameData = getGameData(command.getGameID());
            gameData.game().makeMove(command.getMove());
            gameDAO.updateGame(gameData);
            clients.loadAllClientsGame(command.getGameID(), gameData.game());
            clients.notifyOtherClients(command.getGameID(), client, client.username + " made a move: " + command.getMove());
            sendInCheckStatus(command.getGameID(), gameData.game(), getPlayerColorByCommand(command, client.username), client);
        } catch (InvalidMoveException e) {
            client.sendError("Error: Invalid move");
        } catch (Exception e) {
            client.sendError("Error: " + e.getMessage());
        }
    }

    private void leave(UserGameCommand command, Client client) throws IOException {
        try {
            GameData gameData = getGameData(command.getGameID());
            GameData newGameData;
            if (gameData.blackUsername() != null && gameData.blackUsername().equals(client.username)) {
                newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
            } else if (gameData.whiteUsername() != null && gameData.whiteUsername().equals(client.username)) {
                newGameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
            } else {
                newGameData = gameData;
            }
            gameDAO.updateGame(newGameData);
            clients.notifyOtherClients(command.getGameID(), client, client.username + " has left the game.");
            clients.remove(command.getGameID(), client.username);
        } catch (Exception e) {
            client.sendError("Error: " + e.getMessage());
        }
    }
    //come back to
    private void resign(UserGameCommand command, Client client) throws IOException {
        try {
            if (getPlayerColorByCommand(command, client.username) == null) {
                throw new Exception("Non-players cannot resign.");
            }
            GameData gameData = getGameData(command.getGameID());
            if (!gameData.game().getIfInProgress()) {
                throw new Exception("This game has already ended.");
            }
            gameData.game().setInProgress(false);
            gameDAO.updateGame(gameData);
            clients.notifyAllClients(command.getGameID(), client.username + " has resigned.");
        } catch (Exception e) {
            client.sendError("Error: " + e.getMessage());
        }
    }

    //helpers
    private String getUserNameByCommand(UserGameCommand command, Session session) throws Exception {
        String authToken = command.getAuthToken();
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData != null) {
            return authDAO.getAuthData(authToken).username();
        } else {
            ErrorSMessage error = new ErrorSMessage(ServerMessage.ServerMessageType.ERROR, "Error: Unauthorized");
            session.getRemote().sendString(new Gson().toJson(error));
            throw new Exception("Unauthorized");
        }    }

    private String getPlayerStatusByCommand(UserGameCommand command, String username) throws DataAccessException {
        ChessGame.TeamColor color = getPlayerColorByCommand(command, username);
        switch (color) {
            case WHITE -> {return "white";}
            case BLACK -> {return "black";}
            case null -> {return "observer";}
        }
    }

    private ChessGame.TeamColor getPlayerColorByCommand(UserGameCommand command, String username) throws DataAccessException {
        GameData gameData = gameDAO.getGame(command.getGameID());
        if (gameData.blackUsername().equals(username)) {
            return ChessGame.TeamColor.BLACK;
        } else if (gameData.whiteUsername().equals(username)) {
            return ChessGame.TeamColor.WHITE;
        } else {
            return null;
        }
    }

    private void sendLoadGame(Client client, Integer gameID) throws Exception { //load game req send
        client.sendLoadGame(getGameData(gameID).game());
    }

    private GameData getGameData(Integer gameID) throws DataAccessException {
        GameData gameData = gameDAO.getGame(gameID);
        if (gameData != null) {
            return gameData;
        } else {//might need to refine this later for other tests?
            throw new DataAccessException("Invalid gameID");
        }
    }

    //helpers, valid moves to make, check if in check and send status to clients
    private void canMakeMove(MoveCommand command, Client client) throws Exception {
        ChessGame.TeamColor color = getPlayerColorByCommand(command, client.username);
        ChessGame game = getGameData(command.getGameID()).game();
        ChessGame.TeamColor moveColor = game.getBoard().getPiece(command.getMove().getStartPosition()).getTeamColor();
        if (!game.getIfInProgress()) {
            throw new Exception("This game ended.");
        }
        if (!moveColor.equals(color)) {
            throw new Exception("Piece is wrong color");
        }
    }

    private void sendInCheckStatus(Integer gameID, ChessGame game, ChessGame.TeamColor color, Client client) {
        ChessGame.TeamColor otherTeamColor;
        if (color.equals(ChessGame.TeamColor.WHITE)) {
            otherTeamColor = ChessGame.TeamColor.BLACK;
        } else if (color.equals(ChessGame.TeamColor.BLACK)) {
            otherTeamColor = ChessGame.TeamColor.WHITE;
        } else {return;}
        if (game.isInCheckmate(otherTeamColor)) {
            clients.notifyAllClients(gameID, otherTeamColor.name() + " is in checkmate!");
        } else if (game.isInCheck(otherTeamColor)) {
            clients.notifyAllClients(gameID, otherTeamColor.name() + " is in check.");
        } else if (game.isInStalemate(otherTeamColor)) {
            clients.notifyAllClients(gameID, "Stalemate.");
        }
    }
}