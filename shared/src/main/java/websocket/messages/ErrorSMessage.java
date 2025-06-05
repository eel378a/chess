package websocket.messages;

public class ErrorSMessage extends ServerMessage {
    public String errorMessage;

    public ErrorSMessage(ServerMessageType type, String errorMessage){
        super(type);
        this.errorMessage = errorMessage;
    }
}
