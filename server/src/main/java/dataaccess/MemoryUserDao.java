package dataaccess;
import model.UserData;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;

public class MemoryUserDao implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void clearUsers() {
        users.clear();
    }

    @Override
    public void addUser(UserData user) {
        users.put(user.username(), user);
    }

    @Override
    public Collection<UserData> listUsers() {
        return users.values();
    }
}