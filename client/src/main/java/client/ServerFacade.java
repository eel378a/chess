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

    public void clear() {
    makeRequest("DELETE", "/db", null, null, null);
    }

    public LoginResponse register(UserData user) {
        System.out.println("entered LoginResponse register(UserData)");
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
            //System.out.println("url done");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            //System.out.println("http connection");
            http.setRequestMethod(method);
            //System.out.println("request method set");
            if (authToken != null) {
                http.addRequestProperty("authorization", authToken);
            }
            //System.out.println("auth token worked, authorized");
            //System.out.print(authToken);
            http.setDoOutput(true);
            writeBody(request, http);
            //System.out.println("write body request worked"); //this doesnt happen
            http.connect();
            //System.out.println("http connection was made");
            throwIfNotSuccessful(http);
            //System.out.println("passed throw if not successful http throw");
            return readBody(http, responseType);
        } catch (HttpExcept e) {
            throw e;
        } catch (Exception e) {
            throw new HttpExcept(500, e.getMessage());
        }
    }

    private void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            //System.out.println("request was not null");
            http.addRequestProperty("Content-Type", "application/json");
            //System.out.println("addrequestproperty worked");
            String reqData = new Gson().toJson(request);
            //System.out.println("requested data to json worked");
            // ^printed, so I assume theres something I am missing for output stream to work
            //I am not sure why or what since I am following the curl instructions
            // for writeRequest body - i THINK...
            try (OutputStream requestBody = http.getOutputStream()) {
                //System.out.println("entered try for get output stream");
                requestBody.write(reqData.getBytes());
                //System.out.println("writing requestBody worked");
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
