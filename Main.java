import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Main extends JFrame implements ActionListener {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/homes";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private JButton loginButton;
    private JButton registerButton;
    private JLabel welcomeLabel;
    private JLabel descriptionLabel;

    public Main() {
        setTitle("Delight Homes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 800)); // Set minimum size to prevent content from disappearing

        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        welcomeLabel = new JLabel("Welcome to Delight Homes");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 40));
        welcomeLabel.setForeground(Color.WHITE);

        descriptionLabel = new JLabel(
                "<html>Delight Homes aims to help you find your perfect home. "
                        + "We offer a wide range of properties, from small rooms to large apartments. "
                        + "With us, you'll enjoy a smooth and easy process, along with excellent customer service.<br><br>"
                        + "<b> Why choose us? </b> <br> <br>" +
                        "1. We have many types of homes to choose from.<br> <br>" +
                        "2. Our process is simple and hassle-free.<br> <br>" +
                        "3. We're committed to making sure you're happy.<br> <br></html>"
        );
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        descriptionLabel.setForeground(Color.WHITE);

        loginButton = createStyledButton("Login", null);
        registerButton = createStyledButton("Register", null);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.setOpaque(false);
        gbc.insets = new Insets(10, 10, 40, 10); // Adjusted insets to add spacing between content and buttons

        panel.add(welcomeLabel, gbc);
        panel.add(descriptionLabel, gbc);
        panel.add(buttonPanel, gbc);

        setContentPane(panel);
        setLocationRelativeTo(null);

        loginButton.addActionListener(this);
        registerButton.addActionListener(this);
    }

    private JButton createStyledButton(String text, String iconFilename) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(65, 131, 215));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        if (iconFilename != null && !iconFilename.isEmpty()) {
            ImageIcon icon = new ImageIcon(iconFilename);
            button.setIcon(icon);
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setIconTextGap(20);
        }

        return button;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            Login login = new Login();
            login.setVisible(true);
            this.setVisible(false);
        } else if (e.getSource() == registerButton) {
            Registration registration = new Registration();
            registration.setVisible(true);
            this.setVisible(false);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main mainUI = new Main();
            mainUI.setVisible(true);
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
