package dataaccess;
import model.UserData;
import java.util.HashMap;

public class MemoryUserDao implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    @Override
    public void clearUsers() {
        users.clear();
    }
}