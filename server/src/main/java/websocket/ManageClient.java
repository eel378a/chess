package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;

public class ManageClient {
    private HashMap<Integer, HashMap<String, Client>> clients = new HashMap<>();

    public void add(Integer gameID, Client client) {
        clients.get(gameID).put(client.username, client);
    }

    public void remove(Integer gameID, String username, Session session) {
        clients.get(gameID).remove(username);
    }

    public void notifyOtherClients(Integer gameID, Client currentClient, String message) {
        try {
            for (Client client : clients.get(gameID).values()) {
                if (!client.equals(currentClient)) {
                    client.sendNotification(message);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
