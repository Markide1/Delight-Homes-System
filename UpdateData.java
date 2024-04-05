import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateData {
    // Same as before...

    private static final String URL = null;
    private static final String PASSWORD = null;

    public static void updateUserPassword(String username, String newPassword) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, username, PASSWORD)) {
            String query = "UPDATE Users SET password = ? WHERE username = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, newPassword);
                statement.setString(2, username);
                statement.executeUpdate();
            }
        }
    }
}
