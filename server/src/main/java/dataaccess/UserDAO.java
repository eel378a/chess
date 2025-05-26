package dataaccess;

import model.UserData;
import java.util.Collection;

public interface UserDAO {
    public void clearUsers() throws DataAccessException;
    public void addUser(UserData user)throws DataAccessException;
    public Collection<UserData> listUsers()throws DataAccessException;
    public UserData getUser(String username)throws DataAccessException;
    public UserData getUserByNameAndPassword(String username, String password)throws DataAccessException;
    public void removeUser(String username)throws DataAccessException;
}
