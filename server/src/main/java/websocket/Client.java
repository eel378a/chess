package websocket;

import org.eclipse.jetty.websocket.api.Session;

public class Client {
    public String username;
    public Session session;

    public Client(String username, Session session) {
        this.username = username;
        this.session = session;
    }
}