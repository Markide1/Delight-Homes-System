import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AdminPanel extends JFrame implements ActionListener {
    // Database connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/homes";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    // GUI components
    private JList<String> requestList;
    private DefaultListModel<String> listModel;
    private JScrollPane scrollPane;
    private JButton approveButton;
    private JButton rejectButton;
    private JButton updateButton;
    private JButton addUserButton;
    private JButton removeUserButton;
    private JButton logoutButton; // Added logout button

    public AdminPanel() {
        setTitle("Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and add components using GridBagLayout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Increased Padding between components

        // Create a list to display requests
        listModel = new DefaultListModel<>();
        requestList = new JList<>(listModel);
        scrollPane = new JScrollPane(requestList);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        // Create styled buttons
        approveButton = createStyledButton("Approve Request");
        rejectButton = createStyledButton("Reject Request");
        updateButton = createStyledButton("Update Available Houses/Units");
        addUserButton = createStyledButton("Add User");
        removeUserButton = createStyledButton("Remove User");
        logoutButton = createStyledButton("Logout"); // Added logout button

        // Add components to the panel
        panel.add(scrollPane, gbc);
        panel.add(approveButton, gbc);
        panel.add(rejectButton, gbc);
        panel.add(updateButton, gbc);
        panel.add(addUserButton, gbc);
        panel.add(removeUserButton, gbc);
        panel.add(logoutButton, gbc); // Added logout button

        add(panel);

        // Set frame properties
        setResizable(true); // Allow frame to be resizable
        setMinimumSize(new Dimension(400, 300)); // Set a minimum size for the frame

        // Register action listener for buttons
        approveButton.addActionListener(this);
        rejectButton.addActionListener(this);
        updateButton.addActionListener(this);
        addUserButton.addActionListener(this);
        removeUserButton.addActionListener(this);
        logoutButton.addActionListener(this); // Register logout button

        // Load requests into the list
        loadRequests();

        // Set frame size and center on screen
        pack();
        setLocationRelativeTo(null);
    }

    // Method to create a styled button
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(59, 89, 182));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Action listener for buttons
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == approveButton || e.getSource() == rejectButton) {
            // Approve or reject request
            String status = e.getSource() == approveButton ? "Approved" : "Rejected";
            String selectedRequest = requestList.getSelectedValue();
            if (selectedRequest != null) {
                int requestId = Integer.parseInt(selectedRequest.split(":")[0]); // Extract request ID
                updateRequestStatus(requestId, status);
                // Remove the request from the list
                listModel.removeElement(selectedRequest);
                if (listModel.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No pending requests.");
                    remove(scrollPane); // Remove the JList from the panel
                    validate(); // Validate the panel to reflect changes
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a request to approve or reject.");
            }
        } else if (e.getSource() == updateButton) {
            // Update available houses and units
            int houseId = getUserInput("Enter House ID:");
            int availableHouses = getUserInput("Enter Available Houses:");
            int availableUnits = getUserInput("Enter Available Units:");
            if (houseId != -1 && availableHouses != -1 && availableUnits != -1) {
                updateAvailableHousesAndUnits(houseId, availableHouses, availableUnits);
            }
        } else if (e.getSource() == addUserButton) {
            // Add user
            String username = JOptionPane.showInputDialog("Enter Username:");
            String password = JOptionPane.showInputDialog("Enter Password:");
            String role = JOptionPane.showInputDialog("Enter Role:");
            if (username != null && password != null && role != null) {
                addUser(username, password, role);
            }
        } else if (e.getSource() == removeUserButton) {
            // Remove user
            int userId = getUserInput("Enter User ID:");
            if (userId != -1) {
                removeUser(userId);
            }
        } else if (e.getSource() == logoutButton) {
            // Logout
            dispose(); // Close the AdminPanel window
            // Open the main window (Main class)
            Main main = new Main();
            main.setVisible(true);
        }
    }

    // Method to get user input from JOptionPane
    private int getUserInput(String message) {
        String input = JOptionPane.showInputDialog(message);
        if (input != null && !input.isEmpty()) {
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        }
        return -1; // Return -1 if input is invalid
    }

    // Method to load requests into the list
    private void loadRequests() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT RequestID, Status FROM Requests";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int requestId = resultSet.getInt("RequestID");
                String status = resultSet.getString("Status");
                listModel.addElement(requestId + ": " + status);
            }
            statement.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to load requests: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Method to update request status
    private void updateRequestStatus(int requestId, String status) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "UPDATE Requests SET Status = ? WHERE RequestID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, status);
            statement.setInt(2, requestId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Request status updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update request status.");
            }
            statement.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to update request status: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Method to update available houses and units for a house
    private void updateAvailableHousesAndUnits(int houseId, int availableHouses, int availableUnits) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "UPDATE Houses SET AvailableHouses = ?, AvailableUnits = ? WHERE HouseID = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, availableHouses);
            statement.setInt(2, availableUnits);
            statement.setInt(3, houseId);
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Available houses and units updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update available houses and units.");
            }
            statement.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to update available houses and units: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Method to add a new user
    private void addUser(String username, String password, String role) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "INSERT INTO Users (Username, Password, Role) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, role);
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(this, "User added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add user.");
            }
            statement.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to add user: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Method to remove a user
    private void removeUser(int userId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "DELETE FROM Users WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(this, "User removed successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to remove user.");
            }
            statement.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to remove user: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Ensure that the JDBC driver is loaded
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Create and display the admin panel
        SwingUtilities.invokeLater(() -> {
            // Use try-with-resources to automatically close the connection
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                AdminPanel adminPanel = new AdminPanel();
                adminPanel.setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }
}
