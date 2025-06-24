import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class CurrencyConverterApp extends JFrame implements ActionListener {
    private JComboBox<String> fromCurrencyBox, toCurrencyBox;
    private JTextField amountField, resultField;
    private DefaultListModel<String> historyModel;
    private JList<String> historyList;
    private JButton convertBtn, copyBtn, clearBtn, resetBtn, exitBtn, cloudSyncBtn;

    private final Map<String, Double> rates = Map.ofEntries(
        Map.entry("USD", 1.0),
        Map.entry("INR", 83.0),
        Map.entry("EUR", 0.92),
        Map.entry("GBP", 0.79),
        Map.entry("JPY", 157.5),
        Map.entry("CAD", 1.36),
        Map.entry("AUD", 1.51),
        Map.entry("CNY", 7.25),
        Map.entry("CHF", 0.89),
        Map.entry("SGD", 1.35)
    );

    public CurrencyConverterApp() {
        setTitle("Currency Converter App");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        String[] currencies = rates.keySet().toArray(new String[0]);
        Arrays.sort(currencies);

        fromCurrencyBox = new JComboBox<>(currencies);
        toCurrencyBox = new JComboBox<>(currencies);
        amountField = new JTextField();
        resultField = new JTextField();
        resultField.setEditable(false);

        convertBtn = new JButton("Convert");
        copyBtn = new JButton("Copy Result");
        clearBtn = new JButton("Clear History");
        resetBtn = new JButton("Reset");
        exitBtn = new JButton("Exit");
        cloudSyncBtn = new JButton("Sync to Cloud");

        // Set button and input colors
        Color buttonColor = new Color(255, 228, 240); // Lightest pink
        Color titleBg = new Color(230, 210, 255); // Lighter purple
        JButton[] buttons = { convertBtn, copyBtn, clearBtn, resetBtn, exitBtn, cloudSyncBtn };
        for (JButton btn : buttons) {
            btn.setBackground(buttonColor);
            btn.setForeground(Color.BLACK);
        }

        fromCurrencyBox.setBackground(buttonColor);
        toCurrencyBox.setBackground(buttonColor);
        amountField.setBackground(buttonColor);
        resultField.setBackground(buttonColor);

        convertBtn.addActionListener(this);
        copyBtn.addActionListener(e -> {
            StringSelection selection = new StringSelection(resultField.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            JOptionPane.showMessageDialog(this, "Result copied to clipboard.");
        });
        clearBtn.addActionListener(e -> {
            historyModel.clear();
            saveHistory();
        });
        resetBtn.addActionListener(e -> {
            amountField.setText("");
            resultField.setText("");
            fromCurrencyBox.setSelectedIndex(0);
            toCurrencyBox.setSelectedIndex(0);
        });
        exitBtn.addActionListener(e -> System.exit(0));
        cloudSyncBtn.addActionListener(e -> syncToCloud());

        topPanel.add(new JLabel("From Currency:")); topPanel.add(fromCurrencyBox);
        topPanel.add(new JLabel("To Currency:")); topPanel.add(toCurrencyBox);
        topPanel.add(new JLabel("Amount:")); topPanel.add(amountField);
        topPanel.add(new JLabel("Result:")); topPanel.add(resultField);
        topPanel.add(convertBtn); topPanel.add(copyBtn);

        add(topPanel, BorderLayout.NORTH);

        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        loadHistory();

        JLabel historyLabel = new JLabel("Conversion History:");
        historyLabel.setOpaque(true);
        historyLabel.setBackground(titleBg);
        historyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(historyLabel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(historyList), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        bottomPanel.add(clearBtn);
        bottomPanel.add(resetBtn);
        bottomPanel.add(exitBtn);
        bottomPanel.add(cloudSyncBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        applyLightTheme(topPanel, centerPanel, bottomPanel);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String from = (String) fromCurrencyBox.getSelectedItem();
            String to = (String) toCurrencyBox.getSelectedItem();

            double result = (amount / rates.get(from)) * rates.get(to);
            String resultStr = String.format("%.2f", result);
            resultField.setText(resultStr);

            String entry = amount + " " + from + " = " + resultStr + " " + to;
            historyModel.addElement(entry);
            saveHistory();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        }
    }

    private void saveHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("currency_history.txt"))) {
            for (int i = 0; i < historyModel.size(); i++) {
                writer.write(historyModel.getElementAt(i));
                writer.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadHistory() {
        File file = new File("currency_history.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                historyModel.addElement(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void syncToCloud() {
        Path localFile = Paths.get("currency_history.txt");
        Path cloudFile = Paths.get(System.getProperty("user.home"), "Documents", "currency_history_cloud.txt");
        try {
            Files.copy(localFile, cloudFile, StandardCopyOption.REPLACE_EXISTING);
            JOptionPane.showMessageDialog(this, "History synced to cloud folder successfully.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to sync: " + ex.getMessage());
        }
    }

    private void applyLightTheme(JPanel topPanel, JPanel centerPanel, JPanel bottomPanel) {
        Color lightBg = new Color(245, 230, 255); // lighter purple
        topPanel.setBackground(lightBg);
        centerPanel.setBackground(lightBg);
        bottomPanel.setBackground(lightBg);
        getContentPane().setBackground(lightBg);
    }

    public static void main(String[] args) {
        new CurrencyConverterApp();
    }
}

/**import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class CurrencyConverterApp extends JFrame implements ActionListener {
    private JComboBox<String> fromCurrencyBox, toCurrencyBox;
    private JTextField amountField, resultField;
    private DefaultListModel<String> historyModel;
    private JList<String> historyList;
    private JButton convertBtn, copyBtn, clearBtn, resetBtn, exitBtn, cloudSyncBtn;

    private final Map<String, Double> rates = Map.ofEntries(
        Map.entry("USD", 1.0),
        Map.entry("INR", 83.0),
        Map.entry("EUR", 0.92),
        Map.entry("GBP", 0.79),
        Map.entry("JPY", 157.5),
        Map.entry("CAD", 1.36),
        Map.entry("AUD", 1.51),
        Map.entry("CNY", 7.25),
        Map.entry("CHF", 0.89),
        Map.entry("SGD", 1.35)
    );

    public CurrencyConverterApp() {
        setTitle("Currency Converter App");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        JPanel centerPanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        String[] currencies = rates.keySet().toArray(new String[0]);
        Arrays.sort(currencies);

        fromCurrencyBox = new JComboBox<>(currencies);
        toCurrencyBox = new JComboBox<>(currencies);
        amountField = new JTextField();
        resultField = new JTextField();
        resultField.setEditable(false);

        convertBtn = new JButton("Convert");
        copyBtn = new JButton("Copy Result");
        clearBtn = new JButton("Clear History");
        resetBtn = new JButton("Reset");
        exitBtn = new JButton("Exit");
        cloudSyncBtn = new JButton("Sync to Cloud");

        // Set button and input colors
        Color normalColor = new Color(255, 228, 240); // Lightest pink
        Color pressedColor = new Color(255, 105, 180); // Dark pink when clicked
        Color titleBg = new Color(230, 210, 255); // Lighter purple

        JButton[] buttons = { convertBtn, copyBtn, clearBtn, resetBtn, exitBtn, cloudSyncBtn };
        for (JButton btn : buttons) {
            setButtonColor(btn, normalColor, pressedColor);
        }

        fromCurrencyBox.setBackground(normalColor);
        toCurrencyBox.setBackground(normalColor);
        amountField.setBackground(normalColor);
        resultField.setBackground(normalColor);

        convertBtn.addActionListener(this);
        copyBtn.addActionListener(e -> {
            StringSelection selection = new StringSelection(resultField.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            JOptionPane.showMessageDialog(this, "Result copied to clipboard.");
        });
        clearBtn.addActionListener(e -> {
            historyModel.clear();
            saveHistory();
        });
        resetBtn.addActionListener(e -> {
            amountField.setText("");
            resultField.setText("");
            fromCurrencyBox.setSelectedIndex(0);
            toCurrencyBox.setSelectedIndex(0);
        });
        exitBtn.addActionListener(e -> System.exit(0));
        cloudSyncBtn.addActionListener(e -> syncToCloud());

        topPanel.add(new JLabel("From Currency:")); topPanel.add(fromCurrencyBox);
        topPanel.add(new JLabel("To Currency:")); topPanel.add(toCurrencyBox);
        topPanel.add(new JLabel("Amount:")); topPanel.add(amountField);
        topPanel.add(new JLabel("Result:")); topPanel.add(resultField);
        topPanel.add(convertBtn); topPanel.add(copyBtn);

        add(topPanel, BorderLayout.NORTH);

        historyModel = new DefaultListModel<>();
        historyList = new JList<>(historyModel);
        loadHistory();

        JLabel historyLabel = new JLabel("Conversion History:");
        historyLabel.setOpaque(true);
        historyLabel.setBackground(titleBg);
        historyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(historyLabel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(historyList), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        bottomPanel.add(clearBtn);
        bottomPanel.add(resetBtn);
        bottomPanel.add(exitBtn);
        bottomPanel.add(cloudSyncBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        applyLightTheme(topPanel, centerPanel, bottomPanel);

        setVisible(true);
    }

    private void setButtonColor(JButton button, Color normal, Color pressed) {
        button.setBackground(normal);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);

        button.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                button.setBackground(pressed);
            }

            public void mouseReleased(MouseEvent e) {
                button.setBackground(normal);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(normal);
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String from = (String) fromCurrencyBox.getSelectedItem();
            String to = (String) toCurrencyBox.getSelectedItem();

            double result = (amount / rates.get(from)) * rates.get(to);
            String resultStr = String.format("%.2f", result);
            resultField.setText(resultStr);

            String entry = amount + " " + from + " = " + resultStr + " " + to;
            historyModel.addElement(entry);
            saveHistory();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        }
    }

    private void saveHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("currency_history.txt"))) {
            for (int i = 0; i < historyModel.size(); i++) {
                writer.write(historyModel.getElementAt(i));
                writer.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadHistory() {
        File file = new File("currency_history.txt");
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                historyModel.addElement(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void syncToCloud() {
        Path localFile = Paths.get("currency_history.txt");
        Path cloudFile = Paths.get(System.getProperty("user.home"), "Documents", "currency_history_cloud.txt");
        try {
            Files.copy(localFile, cloudFile, StandardCopyOption.REPLACE_EXISTING);
            JOptionPane.showMessageDialog(this, "History synced to cloud folder successfully.");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Failed to sync: " + ex.getMessage());
        }
    }

    private void applyLightTheme(JPanel topPanel, JPanel centerPanel, JPanel bottomPanel) {
        Color lightBg = new Color(245, 230, 255); // lighter purple
        topPanel.setBackground(lightBg);
        centerPanel.setBackground(lightBg);
        bottomPanel.setBackground(lightBg);
        getContentPane().setBackground(lightBg);
    }

    public static void main(String[] args) {
        new CurrencyConverterApp();
    }
}**/
