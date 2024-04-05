import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReadData {
    // Same as before...

    private static final String PASSWORD = null;
    private static final String URL = null;

    public static void getUser(String username) throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, username, PASSWORD)) {
            String query = "SELECT * FROM Users WHERE username = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                    }
                }
            }
        }
    }
}
