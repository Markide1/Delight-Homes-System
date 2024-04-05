import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame implements ActionListener {
    // Database connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/homes";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    // GUI components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton backButton;

    public Login() {
        setTitle("Login Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and add components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Padding

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        backButton = new JButton("Back"); 

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(usernameField, gbc);

        gbc.gridy = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(loginButton, gbc);

        gbc.gridx = 1;
        panel.add(backButton, gbc); 

        add(panel);

        // Register action listener for buttons
        loginButton.addActionListener(this);
        backButton.addActionListener(this); 

        // Set frame size and center on screen
        pack();
        setLocationRelativeTo(null);
    }

    // Action listener for buttons
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Authenticate user
            if (loginUser(username, password)) {
                // Check user role and open appropriate panel
                if (getUserRole(username).equals("admin")) {
                    openAdminPanel();
                } else {
                    openUserPanel();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.");
            }
        } else if (e.getSource() == backButton) {        
            dispose(); // Close the Login window
            // Open the homepage (Main class)
            Main main = new Main();
            main.setVisible(true);
        }
    }

    // Method to authenticate user
    private boolean loginUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true; // Authentication successful
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false; // Authentication failed
    }

    // Method to get user role
    private String getUserRole(String username) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT role FROM Users WHERE username = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("role"); // Return user role
            }
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return ""; // Return empty string if role not found
    }

    // Method to open the user panel
    private void openUserPanel() {
        UserPanel userPanel = new UserPanel();
        userPanel.setVisible(true);
        // Close the login form
        this.dispose();
    }

    // Method to open the admin panel
    private void openAdminPanel() {
        AdminPanel adminPanel = new AdminPanel();
        adminPanel.setVisible(true);
        // Close the login form
        this.dispose();
    }

    public static void main(String[] args) {
        // Ensure that the JDBC driver is loaded
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Create and display the login form
        SwingUtilities.invokeLater(() -> {
            Login form = new Login();
            form.setVisible(true);
        });
    }
}
