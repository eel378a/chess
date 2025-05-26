import dataaccess.DataAccessException;
import dataaccess.SqlUserDao;
import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class UserDAOTests {
    UserDAO users;

    @BeforeEach
    public void initializeDatabase() throws DataAccessException {
        users = new SqlUserDao();
    }

    @Test
    public void addUser() {
        UserData user = new UserData("name", "password", "email");
        assertDoesNotThrow(() -> users.addUser(user));
    }
}
