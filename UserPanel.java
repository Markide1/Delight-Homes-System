import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserPanel extends JFrame implements ActionListener {
    // Database connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/homes";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    // GUI components
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

        applyButton = createButton("Apply for a House");
        cancelButton = createButton("Cancel House Request");
        checkStatusButton = createButton("Check Status"); // Added check status button
        logoutButton = createButton("Logout");

        panel.add(applyButton, gbc);
        panel.add(cancelButton, gbc);
        panel.add(checkStatusButton, gbc); 
        panel.add(logoutButton, gbc); // Added logout button

        add(panel);

        applyButton.addActionListener(this);
        cancelButton.addActionListener(this);
        checkStatusButton.addActionListener(this); 
        logoutButton.addActionListener(this); // Added action listener for logout button

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
        if (e.getSource() == applyButton) {
            applyForHousePrompt();
        } else if (e.getSource() == cancelButton) {
            cancelHouseRequestPrompt();
        } else if (e.getSource() == checkStatusButton) {
            checkStatus();
        } else if (e.getSource() == logoutButton) {
            dispose();
            // Open main page
            Main main = new Main();
            main.setVisible(true);
        }
    }

    private void applyForHousePrompt() {
        List<House> availableHouses = getAvailableHouses();
        if (availableHouses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available houses at the moment.");
        } else {
            StringBuilder message = new StringBuilder("Available Houses:\n");
            for (House house : availableHouses) {
                message.append("House ID: ").append(house.getId())
                        .append(", Description: ").append(house.getDescription())
                        .append(", Available Units: ").append(house.getAvailableUnits())
                        .append("\n");
            }
            String selectedHouseId = JOptionPane.showInputDialog(this, message.toString(), "Enter House ID");
            if (selectedHouseId != null && !selectedHouseId.isEmpty()) {
                try {
                    int houseId = Integer.parseInt(selectedHouseId);
                    applyForHouse(houseId);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input for House ID.");
                }
            }
        }
    }

    private List<House> getAvailableHouses() {
        List<House> availableHouses = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM Houses WHERE AvailableUnits > 0";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                while (resultSet.next()) {
                    int houseId = resultSet.getInt("HouseID");
                    String description = resultSet.getString("Description");
                    int availableUnits = resultSet.getInt("AvailableUnits");
                    availableHouses.add(new House(houseId, description, availableUnits));
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to fetch available houses: " + ex.getMessage());
            ex.printStackTrace();
        }
        return availableHouses;
    }

    private void applyForHouse(int houseId) {
        int userId = getUserId();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "INSERT INTO Requests (HouseID, UserID, Status) VALUES (?, ?, 'Pending')";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, houseId);
                statement.setInt(2, userId);
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    JOptionPane.showMessageDialog(this, "Application submitted successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to submit application.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to submit application: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void cancelHouseRequestPrompt() {
        List<Request> userRequests = getUserRequests();
        if (userRequests.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no pending requests to cancel.");
        } else {
            StringBuilder message = new StringBuilder("Your Pending Requests:\n");
            for (Request request : userRequests) {
                message.append("Request ID: ").append(request.getRequestId())
                        .append(", House ID: ").append(request.getHouseId())
                        .append(", Status: ").append(request.getStatus())
                        .append("\n");
            }
            String selectedRequestId = JOptionPane.showInputDialog(this, message.toString(), "Enter Request ID");
            if (selectedRequestId != null && !selectedRequestId.isEmpty()) {
                try {
                    int requestId = Integer.parseInt(selectedRequestId);
                    cancelHouseRequest(requestId);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input for Request ID.");
                }
            }
        }
    }

    private List<Request> getUserRequests() {
        List<Request> userRequests = new ArrayList<>();
        int userId = getUserId();
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM Requests WHERE UserID = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int requestId = resultSet.getInt("RequestID");
                        int houseId = resultSet.getInt("HouseID");
                        String status = resultSet.getString("Status");
                        userRequests.add(new Request(requestId, houseId, status));
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to fetch user requests: " + ex.getMessage());
            ex.printStackTrace();
        }
        return userRequests;
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

class House {
    private int id;
    private String description;
    private int availableUnits;

    public House(int id, String description, int availableUnits) {
        this.id = id;
        this.description = description;
        this.availableUnits = availableUnits;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public int getAvailableUnits() {
        return availableUnits;
    }
}

class Request {
    private int requestId;
    private int houseId;
    private String status;

    public Request(int requestId, int houseId, String status) {
        this.requestId = requestId;
        this.houseId = houseId;
        this.status = status;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getHouseId() {
        return houseId;
    }

    public String getStatus() {
        return status;
    }
}
