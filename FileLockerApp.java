import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.awt.datatransfer.DataFlavor;
import java.security.*;

public class FileLockerApp extends JFrame {
    private JTextField fileField;
    private JPasswordField passwordField;
    private JLabel strengthLabel;
    private JButton browseBtn, encryptBtn, decryptBtn, themeBtn, exitBtn;
    private File selectedFile;
    private boolean darkTheme = true;

    public FileLockerApp() {
        setUndecorated(true);
        setLayout(new BorderLayout());
        setSize(500, 300);
        setLocationRelativeTo(null);

        // === Top Title Bar ===
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Color.DARK_GRAY);
        JLabel titleLabel = new JLabel("File Locker App", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleBar.add(titleLabel, BorderLayout.CENTER);
        add(titleBar, BorderLayout.NORTH);

        // === Center Form ===
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JPanel filePanel = new JPanel();
        fileField = new JTextField(25);
        browseBtn = new JButton("Browse");
        styleButton(browseBtn);
        JLabel fileLabel = new JLabel("File: ");
        filePanel.add(fileLabel);
        filePanel.add(fileField);
        filePanel.add(browseBtn);

        JPanel passPanel = new JPanel();
        passwordField = new JPasswordField(20);
        JLabel passLabel = new JLabel("Password: ");
        passPanel.add(passLabel);
        passPanel.add(passwordField);

        strengthLabel = new JLabel("Strength: ");
        passPanel.add(strengthLabel);

        passwordField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateStrength(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateStrength(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateStrength(); }
        });

        JPanel actionPanel = new JPanel();
        encryptBtn = new JButton("Encrypt");
        decryptBtn = new JButton("Decrypt");
        styleButton(encryptBtn);
        styleButton(decryptBtn);
        actionPanel.add(encryptBtn);
        actionPanel.add(decryptBtn);

        centerPanel.add(filePanel);
        centerPanel.add(passPanel);
        centerPanel.add(actionPanel);
        applyTheme(centerPanel);
        add(centerPanel, BorderLayout.CENTER);

        // === Bottom Exit and Theme Switch ===
        JPanel bottomPanel = new JPanel();
        themeBtn = new JButton("Toggle Theme");
        exitBtn = new JButton("Exit");
        styleButton(themeBtn);
        styleButton(exitBtn);
        bottomPanel.add(themeBtn);
        bottomPanel.add(exitBtn);
        applyTheme(bottomPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        browseBtn.addActionListener(e -> chooseFile());
        encryptBtn.addActionListener(e -> processFile(true));
        decryptBtn.addActionListener(e -> processFile(false));
        exitBtn.addActionListener(e -> System.exit(0));
        themeBtn.addActionListener(e -> toggleTheme());

        fileField.setDragEnabled(true);
        fileField.setTransferHandler(new TransferHandler() {
            public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }
            public boolean importData(TransferSupport support) {
                try {
                    java.util.List<File> droppedFiles = (java.util.List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (!droppedFiles.isEmpty()) {
                        selectedFile = droppedFiles.get(0);
                        fileField.setText(selectedFile.getAbsolutePath());
                        return true;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        });

        setVisible(true);
    }

    private void updateStrength() {
        String password = new String(passwordField.getPassword());
        String strength = "Weak";
        Color color = Color.RED;
        if (password.length() > 8 && password.matches(".*[A-Z].*") && password.matches(".*[0-9].*") && password.matches(".*[!@#$%^&*()].*")) {
            strength = "Strong";
            color = Color.GREEN;
        } else if (password.length() >= 6) {
            strength = "Medium";
            color = Color.ORANGE;
        }
        strengthLabel.setText("Strength: " + strength);
        strengthLabel.setForeground(color);
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(30, 144, 255));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

    private void chooseFile() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = chooser.getSelectedFile();
            fileField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void processFile(boolean encrypt) {
        if (selectedFile == null || passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Please select a file and enter password.");
            return;
        }
        try {
            String password = new String(passwordField.getPassword());
            byte[] key = password.getBytes("UTF-8");
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            key = sha.digest(key);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, secretKey);

            FileInputStream fis = new FileInputStream(selectedFile);
            byte[] inputBytes = fis.readAllBytes();
            fis.close();

            byte[] outputBytes = cipher.doFinal(inputBytes);

            String newName = selectedFile.getAbsolutePath();
            if (encrypt) newName += ".locked";
            else if (newName.endsWith(".locked")) newName = newName.replace(".locked", ".unlocked");
            else newName += ".decrypted";

            FileOutputStream fos = new FileOutputStream(newName);
            fos.write(outputBytes);
            fos.close();

            fileField.setText("");
            passwordField.setText("");
            strengthLabel.setText("Strength: ");

            JOptionPane.showMessageDialog(this, (encrypt ? "Encrypted" : "Decrypted") + " successfully!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void toggleTheme() {
        darkTheme = !darkTheme;
        applyTheme(this.getContentPane());
        repaint();
    }

    private void applyTheme(Container container) {
        Color bg = darkTheme ? new Color(60, 63, 65) : Color.WHITE;
        Color fg = darkTheme ? Color.WHITE : Color.BLACK;
        for (Component comp : ((JPanel) container).getComponents()) {
            comp.setBackground(bg);
            comp.setForeground(fg);
            if (comp instanceof JPanel) {
                applyTheme((JPanel) comp);
            }
        }
        container.setBackground(bg);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FileLockerApp::new);
    }
}
