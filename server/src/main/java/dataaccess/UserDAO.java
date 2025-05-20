package dataaccess;

import model.UserData;
import java.util.Collection;

public interface UserDAO {
    public void clearUsers();
    public void addUser(UserData user);
    public Collection<UserData> listUsers();
    public UserData getUser(String username);
    public UserData getUserByNameAndPassword(String username, String password);
}
