package websocket.messages;
import chess.ChessGame;

public class LoadSMessage extends ServerMessage{
    public ChessGame game;

    public LoadSMessage(ServerMessageType type, ChessGame game){
        super(type);
        this.game = game;
    }
}
