import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class UserPanel extends JFrame implements ActionListener {
    // Database connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/homes";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    // GUI components
    private JButton viewHousesButton;
    private JButton applyButton;
    private JButton cancelButton;
    private JButton checkStatusButton; // Added check status button
    private JButton logoutButton;

    public UserPanel() {
        setTitle("User Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setMinimumSize(new Dimension(400, 300));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        viewHousesButton = createButton("View Available Houses");
        applyButton = createButton("Apply for a House");
        cancelButton = createButton("Cancel House Request");
        checkStatusButton = createButton("Check Status"); // Added check status button
        logoutButton = createButton("Logout");

        panel.add(viewHousesButton, gbc);
        panel.add(applyButton, gbc);
        panel.add(cancelButton, gbc);
        panel.add(checkStatusButton, gbc); // Added check status button
        panel.add(logoutButton, gbc);

        add(panel);

        viewHousesButton.addActionListener(this);
        applyButton.addActionListener(this);
        cancelButton.addActionListener(this);
        checkStatusButton.addActionListener(this); // Register check status button
        logoutButton.addActionListener(this);

        pack();
        setLocationRelativeTo(null);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewHousesButton) {
            viewAvailableHouses();
        } else if (e.getSource() == applyButton) {
            applyForHousePrompt();
        } else if (e.getSource() == cancelButton) {
            cancelHouseRequestPrompt();
        } else if (e.getSource() == checkStatusButton) {
            checkStatus();
        } else if (e.getSource() == logoutButton) {
            dispose();
            Main main = new Main();
            main.setVisible(true);
        }
    }

    private void viewAvailableHouses() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM Houses WHERE AvailableUnits > 0";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                StringBuilder message = new StringBuilder("Available Houses:\n");
                while (resultSet.next()) {
                    int houseId = resultSet.getInt("HouseID");
                    String address = resultSet.getString("Address");
                    int availableUnits = resultSet.getInt("AvailableUnits");
                    message.append("HouseID: ").append(houseId).append(", Address: ").append(address)
                            .append(", Available Units: ").append(availableUnits).append("\n");
                }
                if (message.toString().equals("Available Houses:\n")) {
                    JOptionPane.showMessageDialog(this, "No available houses at the moment.");
                } else {
                    JOptionPane.showMessageDialog(this, message.toString());
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to fetch available houses: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void applyForHousePrompt() {
        String houseIdString = JOptionPane.showInputDialog("Enter HouseID:");
        if (houseIdString != null && !houseIdString.isEmpty()) {
            try {
                int houseId = Integer.parseInt(houseIdString);
                int requestId = applyForHouse(houseId);
                if (requestId != -1) {
                    JOptionPane.showMessageDialog(this, "Application submitted successfully! Your Request ID is: " + requestId);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to submit application.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input for HouseID.");
            }
        }
    }

    private int applyForHouse(int houseId) {
        int userId = getUserId();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "INSERT INTO Requests (HouseID, UserID, Status) VALUES (?, ?, 'Pending')";
            try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, houseId);
                statement.setInt(2, userId);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to submit application: " + ex.getMessage());
            ex.printStackTrace();
        }
        return -1;
    }

    private void cancelHouseRequestPrompt() {
        String requestIdString = JOptionPane.showInputDialog("Enter Request ID:");
        if (requestIdString != null && !requestIdString.isEmpty()) {
            try {
                int requestId = Integer.parseInt(requestIdString);
                cancelHouseRequest(requestId);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input for Request ID.");
            }
        }
    }

    private void cancelHouseRequest(int requestId) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "DELETE FROM Requests WHERE RequestID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, requestId);
                int rowsDeleted = statement.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Request canceled successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to cancel request.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to cancel request: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void checkStatus() {
        int userId = getUserId();
        if (userId == -1) {
            JOptionPane.showMessageDialog(this, "You need to apply for a house first!");
            return;
        }
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM Requests WHERE UserID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    boolean hasRequests = false;
                    StringBuilder statusMessage = new StringBuilder("Your Request Status:\n");
                    while (resultSet.next()) {
                        hasRequests = true;
                        int requestId = resultSet.getInt("RequestID");
                        int houseId = resultSet.getInt("HouseID");
                        String status = resultSet.getString("Status");
                        statusMessage.append("Request ID: ").append(requestId)
                                .append(", House ID: ").append(houseId)
                                .append(", Status: ").append(status).append("\n");
                    }
                    if (!hasRequests) {
                        JOptionPane.showMessageDialog(this, "You have no pending requests.");
                    } else {
                        JOptionPane.showMessageDialog(this, statusMessage.toString());
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to check request status: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private int getUserId() {
        // For demonstration, return a dummy user ID
        return 1;
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            UserPanel userPanel = new UserPanel();
            userPanel.setVisible(true);
        });
    }
}
