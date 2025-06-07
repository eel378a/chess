package client;

import data.LoginResponse;
import data.CreateGameRequest;
import data.CreateGameResponse;
import data.ListGameResponse;
import data.JoinGameReq;

import model.GameData;
import model.UserData;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl= url;
    }
    public String getServerUrl() {
        return serverUrl;
    }

    public void clear() {
    makeRequest("DELETE", "/db", null, null, null);
    }

    public LoginResponse register(UserData user) {
        return makeRequest("POST", "/user", user, null, LoginResponse.class);
    }

    public LoginResponse login(String username, String password) {
        return makeRequest("POST", "/session", new UserData(username, password, null), null, LoginResponse.class);
    }

    public void logout(String authToken) {
        makeRequest("DELETE", "/session", null, authToken, null);
    }

    public int createGame(String gameName, String authToken) {
        CreateGameResponse response = makeRequest("POST", "/game", new CreateGameRequest(gameName), authToken, CreateGameResponse.class);
        return response.gameID();
    }

    public Collection<GameData> listGames(String authToken) {
    return makeRequest("GET", "/game", null, authToken, ListGameResponse.class).games();
    }

    public void joinGame(String playerColor, int gameID, String authToken) {
        JoinGameReq req = new JoinGameReq(playerColor, gameID);
        makeRequest("PUT", "/game", req, authToken, null);
    }

    //makeRequest and involved fns - see webapi.md
    private <T> T makeRequest(String method, String path, Object request, String authToken, Class<T> responseType) throws HttpExcept {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if (authToken != null) {
                http.addRequestProperty("authorization", authToken);
            }
            http.setDoOutput(true);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            //System.out.println("did not throw error:)");
            return readBody(http, responseType);
        } catch (HttpExcept e) {
            throw e;
        } catch (Exception e) {
            throw new HttpExcept(500, e.getMessage());
        }
    }

    private void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream requestBody = http.getOutputStream()) {
                requestBody.write(reqData.getBytes());
            }
        }
    }

    private <T> T readBody(HttpURLConnection http, Class<T> responseType) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream body = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(body);
                if (responseType != null) {
                    response = new Gson().fromJson(reader, responseType);
                }
            }
        }
        return response;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, HttpExcept {
        int status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream responseError = http.getErrorStream()) {
                if (responseError != null) {
                    throw HttpExcept.fromStream(responseError, status);
                }
            }
            throw new HttpExcept(status, "Error: " + status);
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
