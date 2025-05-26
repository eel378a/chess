package dataaccess;
import model.AuthData;
import java.util.Collection;

public interface AuthDAO {
    public void clearAuthTokens() throws DataAccessException;
    public void addAuthToken(AuthData authData)throws DataAccessException;
    public Collection<AuthData> listAuthTokens()throws DataAccessException;
    public AuthData getAuthData(String authToken)throws DataAccessException;
    public void removeAuthData(String authToken)throws DataAccessException;
}
