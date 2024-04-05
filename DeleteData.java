import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteData {
    // Same as before...

    private static final String URL = null;
    private static final String PASSWORD = null;

    public static void deleteUser(String username) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, username, PASSWORD)) {
            String query = "DELETE FROM Users WHERE username = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, username);
                statement.executeUpdate();
            }
        }
    }
}
