import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.Year; // Add this import statement

public class Main extends JFrame implements ActionListener {
    // Database connection parameters
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/homes";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    // GUI components
    private JButton loginButton;
    private JButton registerButton;
    private JLabel welcomeLabel;
    private JTextArea descriptionArea;

    public Main() {
        setTitle("Delight Homes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); 

        // Create and add components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Increased Padding between components

        welcomeLabel = new JLabel("Welcome to Delight Homes");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32)); // Larger font size for welcome message

        descriptionArea = new JTextArea(
                "Delight Homes aims to provide the best housing solutions tailored to your needs. "
                        + "We offer a wide range of properties, from cozy apartments to luxurious villas, "
                        + "ensuring that you find your dream home with us. Join us and experience the delight of finding your perfect abode.\n\n"
                        + "Why Choose Delight Homes?\n" +
                        "1. Vast selection of properties tailored to your needs.\n" +
                        "2. Experienced agents dedicated to helping you find your dream home.\n" +
                        "3. Easy and hassle-free transaction process.\n" +
                        "4. Exceptional customer service ensuring your satisfaction."
        );
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 18)); // Larger font size for description
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(panel.getBackground());

        loginButton = createStyledButton("Login");
        registerButton = createStyledButton("Register");

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // 1 row, 2 columns, horizontal gap of 10
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        panel.add(welcomeLabel, gbc);
        panel.add(Box.createVerticalStrut(20)); // Add vertical spacing
        panel.add(descriptionArea, gbc);
        panel.add(Box.createVerticalStrut(20)); // Add vertical spacing
        panel.add(buttonPanel, gbc); // Add the button panel instead of individual buttons

        JLabel footerLabel = new JLabel("© " + Year.now().getValue() + " Delight Homes. All rights reserved. Thank you for choosing us!");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.anchor = GridBagConstraints.PAGE_END; // Align to the bottom of the panel
        gbc.insets = new Insets(10, 10, 10, 10); // Adjust insets for footer
        panel.add(footerLabel, gbc);

        setContentPane(panel);
        setLocationRelativeTo(null); // Center the frame on the screen

        // Register action listeners for buttons
        loginButton.addActionListener(this);
        registerButton.addActionListener(this);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false); // Remove focus border
        button.setBackground(new Color(65, 131, 215)); // Set background color
        button.setForeground(Color.WHITE); // Set text color
        button.setFont(new Font("Arial", Font.BOLD, 24)); // Larger font size for buttons
        button.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30)); // Set padding
        return button;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            // Open login window
            Login login = new Login();
            login.setVisible(true);
            this.setVisible(false); // Hide the main UI
        } else if (e.getSource() == registerButton) {
            // Open registration window
            Registration registration = new Registration();
            registration.setVisible(true);
            this.setVisible(false); // Hide the main UI
        }
    }

    public static void main(String[] args) {
        // Create and display the main UI
        SwingUtilities.invokeLater(() -> {
            Main mainUI = new Main();
            mainUI.setVisible(true);
            // Establish database connection
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
                // Connection successful, do something if needed
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
