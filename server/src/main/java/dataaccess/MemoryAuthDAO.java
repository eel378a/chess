package dataaccess;
import java.util.HashMap;
import model.AuthData;
import java.util.List;
import java.util.Collection;

public class MemoryAuthDAO implements AuthDAO {
    final private HashMap<String, AuthData> authTokens = new HashMap<>();

    @Override
    public AuthData getAuthData(String authToken) {
        return authTokens.get(authToken);
    }

    @Override
    public void clearAuthTokens() {
        authTokens.clear();
    }

    @Override
    public void addAuthToken(AuthData authData) {
        authTokens.put(authData.authToken(), authData);
    }

    @Override
    public Collection<AuthData> listAuthTokens() {
        return authTokens.values();
    }

    @Override
    public void removeAuthData(String authToken) {
        authTokens.remove(authToken);
    }
}

