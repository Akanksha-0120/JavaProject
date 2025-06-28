import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class DisasterManagementSystem extends JFrame {
    private static final String REPORT_FILE = "disaster_reports.txt";
    private static final String USER_FILE = "users.txt";
    private String currentUser = "";
    private String currentRole = "";
    private String currentFirstName = "";
    private String currentLastName = "";

    public DisasterManagementSystem() {
        setUndecorated(true);
        showInitialPage();
    }

    private void showInitialPage() {
        JFrame frame = new JFrame();
        frame.setUndecorated(true);
        frame.setSize(400, 250);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Disaster Management System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        frame.add(title, BorderLayout.NORTH);

        JButton createAccountBtn = new JButton("Create Account");
        createAccountBtn.addActionListener(e -> {
            frame.dispose();
            showSignupDialog();
        });

        JButton loginBtn = new JButton("Login (Already Created Account)");
        loginBtn.addActionListener(e -> {
            frame.dispose();
            showLoginDialog();
        });

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        centerPanel.add(createAccountBtn);
        centerPanel.add(loginBtn);

        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        centerPanel.add(exitBtn);

        frame.add(centerPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void showSignupDialog() {
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        String[] roles = {"Client", "Volunteer"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        Object[] fields = {
            "First Name:", firstNameField,
            "Last Name:", lastNameField,
            "Username:", usernameField,
            "Password:", passwordField,
            "Role:", roleBox
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Create Account", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            boolean exists = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 5 && parts[2].equals(username) && parts[3].equals(password)) {
                        exists = true;
                        break;
                    }
                }
            } catch (IOException ignored) {}

            if (exists) {
                JOptionPane.showMessageDialog(this, "Account already exists with the same username and password.", "Error", JOptionPane.ERROR_MESSAGE);
                showSignupDialog();
                return;
            }

            String userData = firstNameField.getText() + "," + lastNameField.getText() + "," + username + "," + password + "," + roleBox.getSelectedItem();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
                writer.write(userData);
                writer.newLine();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to save account.", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            showLoginDialog();
        } else {
            showInitialPage();
        }
    }

    private void showLoginDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] fields = {
            "Username:", usernameField,
            "Password:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, fields, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            boolean validUser = false;
            try (BufferedReader reader = new BufferedReader(new FileReader(USER_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 5 && parts[2].equals(username) && parts[3].equals(password)) {
                        validUser = true;
                        currentFirstName = parts[0];
                        currentLastName = parts[1];
                        currentUser = parts[2];
                        currentRole = parts[4];
                        break;
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Failed to read user data.", "Error", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }

            if (validUser) {
                JOptionPane.showMessageDialog(this, "Welcome to Disaster Management System as " + currentRole);
                initComponents();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
                showInitialPage();
            }
        } else {
            showInitialPage();
        }
    }

    private void initComponents() {
        setTitle("Disaster Management System");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel headerLabel = new JLabel("Disaster Management System", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setPreferredSize(new Dimension(200, getHeight()));
        profilePanel.setBorder(BorderFactory.createTitledBorder("User Profile"));

        JLabel profilePic = new JLabel(new ImageIcon("profile_icon.png"));
        profilePic.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("First Name: " + currentFirstName);
        JLabel lastLabel = new JLabel("Last Name: " + currentLastName);
        JLabel userLabel = new JLabel("Username: " + currentUser);
        JLabel roleLabel = new JLabel("Role: " + currentRole);

        for (JLabel label : new JLabel[]{nameLabel, lastLabel, userLabel, roleLabel}) {
            label.setFont(new Font("SansSerif", Font.BOLD, 14));
            label.setHorizontalAlignment(SwingConstants.LEFT);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.addActionListener(e -> {
            dispose();
            new DisasterManagementSystem();
        });

        profilePanel.add(profilePic);
        profilePanel.add(Box.createVerticalStrut(10));
        profilePanel.add(nameLabel);
        profilePanel.add(lastLabel);
        profilePanel.add(userLabel);
        profilePanel.add(roleLabel);
        profilePanel.add(Box.createVerticalGlue());
        profilePanel.add(logoutBtn);

        add(profilePanel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] buttonNames = {"Report Disaster", "Emergency Contacts", "Live Updates", "Relief Centers", "Admin Panel", "Exit"};
        JButton[] buttons = new JButton[buttonNames.length];

        for (int i = 0; i < buttonNames.length; i++) {
            buttons[i] = new JButton(buttonNames[i]);
            buttons[i].setFont(new Font("SansSerif", Font.BOLD, 16));
            buttonPanel.add(buttons[i]);
        }

        buttons[0].addActionListener(e -> openDisasterReportForm());
        buttons[1].addActionListener(e -> showTextAreaFrame("Emergency Contacts", "Police: 100\nFire: 101\nAmbulance: 102\nDisaster Helpline: 108\nRed Cross: 109\n"));
        buttons[2].addActionListener(e -> showTextAreaFrame("Live Updates", loadFileContent(REPORT_FILE, "No live updates available.")));
        buttons[3].addActionListener(e -> showTextAreaFrame("Relief Centers", "1. City Hall - Shelter\n2. Central School - Food Supply\n3. Community Center - Medical Aid\n(More updates coming soon)"));
        buttons[4].addActionListener(e -> showTextAreaFrame("Admin Panel", loadFileContent(REPORT_FILE, "No reports available.")));
        buttons[5].addActionListener(e -> System.exit(0));

        add(buttonPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void openDisasterReportForm() {
        JFrame form = new JFrame("Report a Disaster");
        form.setSize(400, 400);
        form.setLocationRelativeTo(null);
        form.setLayout(new GridLayout(6, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Earthquake", "Flood", "Fire", "Cyclone", "Other"});
        JTextArea descriptionArea = new JTextArea();

        JButton submitBtn = new JButton("Submit");
        submitBtn.addActionListener(e -> {
            String report = String.format("Name: %s\nLocation: %s\nType: %s\nDescription: %s\n---------------------------\n",
                    nameField.getText(), locationField.getText(), typeBox.getSelectedItem(), descriptionArea.getText());
            try (FileWriter writer = new FileWriter(REPORT_FILE, true)) {
                writer.write(report);
                JOptionPane.showMessageDialog(form, "Report submitted successfully!");
                form.dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(form, "Failed to save report.");
            }
        });

        form.add(new JLabel("Full Name:"));
        form.add(nameField);
        form.add(new JLabel("Location:"));
        form.add(locationField);
        form.add(new JLabel("Disaster Type:"));
        form.add(typeBox);
        form.add(new JLabel("Description:"));
        form.add(new JScrollPane(descriptionArea));
        form.add(new JLabel());
        form.add(submitBtn);

        form.setVisible(true);
    }

    private void showTextAreaFrame(String title, String content) {
        JFrame frame = new JFrame(title);
        frame.setSize(450, 350);
        frame.setLocationRelativeTo(null);
        JTextArea area = new JTextArea(content);
        area.setEditable(false);
        frame.add(new JScrollPane(area));
        frame.setVisible(true);
    }

    private String loadFileContent(String filePath, String defaultMsg) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            return defaultMsg;
        }
        return content.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DisasterManagementSystem::new);
    }
}


