import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    // JDBC URL, username, and password of MySQL server
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/homes";
    private static final String USERNAME = "root"; // Assuming the default username is "root"
    private static final String PASSWORD = "root"; // Assuming the default password is blank

    public static void main(String[] args) {
        try {
            // Establishing a connection to the database
            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);

            // Creating a statement to execute SQL queries
            Statement statement = connection.createStatement();

            // Query to select all users
            String query = "SELECT * FROM Users";

            // Executing the query
            ResultSet resultSet = statement.executeQuery(query);

            // Printing the results
            while (resultSet.next()) {
                int userID = resultSet.getInt("UserID");
                String username = resultSet.getString("Username");
                String userType = resultSet.getString("UserType");
                
                // Example: Print retrieved user data
                System.out.println("UserID: " + userID + ", Username: " + username + ", UserType: " + userType);
            }

            // Closing resources
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
