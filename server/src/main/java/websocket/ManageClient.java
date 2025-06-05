package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;

public class ManageClient {
    private HashMap<Integer, HashMap<String, Client>> clients = new HashMap<>();

    public void add(Integer gameID, String username, Session session) {
        clients.get(gameID).put(username, new Client(username, session));
    }

    public void remove(Integer gameID, String username, Session session) {
        clients.get(gameID).remove(username);
    }
}
