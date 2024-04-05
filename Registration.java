import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Registration extends JFrame implements ActionListener {
    // Database connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/homes";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    // GUI components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton registerButton;
    private JButton backButton;

    public Registration() {
        setTitle("User Registration Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and add components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Padding

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        registerButton = new JButton("Register");
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
        panel.add(registerButton, gbc);

        gbc.gridx = 1;
        panel.add(backButton, gbc); // Add back button

        add(panel);

        // Register action listener for the register button
        registerButton.addActionListener(this);
        backButton.addActionListener(this); 

        // Set frame size and center on screen
        pack();
        setLocationRelativeTo(null);
    }

    // Action listener for buttons
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerButton) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Insert user data into the database
            if (insertUserData(username, password)) {
                // Clear fields after successful registration
                clearFields();

                // Open UserPanel with the registered username
                openUserPanel(username);

                // Close the registration form
                dispose();
            }
        } else if (e.getSource() == backButton) {
            // Open the homepage (Main class)
            Main main = new Main();
            main.setVisible(true);

            // Close the registration form
            dispose();
        }
    }

    // Method to insert user data into the database
    private boolean insertUserData(String username, String password) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "INSERT INTO Users (username, password, role) VALUES (?, ?, 'User')";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                return true;
            }
            statement.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Registration failed!");
            ex.printStackTrace();
        }
        return false;
    }

    // Method to clear all input fields
    private void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
    }

    // Method to open the UserPanel with the registered username
    private void openUserPanel(String username) {
        UserPanel userPanel = new UserPanel(); // Pass the username to the UserPanel constructor
        userPanel.setVisible(true);
    }

    public static void main(String[] args) {
        // Ensure that the JDBC driver is loaded
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Create and display the registration form
        SwingUtilities.invokeLater(() -> {
            Registration form = new Registration();
            form.setVisible(true);
        });
    }
}
