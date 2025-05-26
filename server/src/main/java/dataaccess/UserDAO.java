package dataaccess;

import model.UserData;
import java.util.Collection;

public interface UserDAO {
    void clearUsers() throws DataAccessException;
    void addUser(UserData user)throws DataAccessException;
    Collection<UserData> listUsers()throws DataAccessException;
    UserData getUser(String username)throws DataAccessException;
    UserData getUserByNameAndPassword(String username, String password)throws DataAccessException;
    void removeUser(String username)throws DataAccessException;
}
