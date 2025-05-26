package dataaccess;
import model.AuthData;
import java.util.Collection;

public interface AuthDAO {
    void clearAuthTokens() throws DataAccessException;
    void addAuthToken(AuthData authData)throws DataAccessException;
    Collection<AuthData> listAuthTokens()throws DataAccessException;
    AuthData getAuthData(String authToken)throws DataAccessException;
    void removeAuthData(String authToken)throws DataAccessException;
}
