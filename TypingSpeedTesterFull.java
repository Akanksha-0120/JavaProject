import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class TypingSpeedTesterFull {
    public static void main(String[] args) {
        new LoginSignup();
    }
}

class LoginSignup extends JFrame {
    JTextField userField;
    JPasswordField passField;
    JButton loginBtn, signupBtn;
    File userFile = new File("users.txt");

    public LoginSignup() {
        setTitle("Login / Signup");
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        userField = new JTextField();
        passField = new JPasswordField();
        loginBtn = new JButton("Login");
        signupBtn = new JButton("Signup");

        setLayout(new GridLayout(5, 1));
        add(new JLabel("Username:"));
        add(userField);
        add(new JLabel("Password:"));
        add(passField);
        JPanel btnPanel = new JPanel();
        btnPanel.add(loginBtn);
        btnPanel.add(signupBtn);
        add(btnPanel);

        loginBtn.addActionListener(e -> login());
        signupBtn.addActionListener(e -> signup());

        setVisible(true);
    }

    void login() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword()).trim();

        try (BufferedReader br = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals(user + ":" + pass)) {
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    dispose();
                    new TypingSpeedTester(user);
                    return;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, "Invalid credentials.");
    }

    void signup() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            if (!userFile.exists()) userFile.createNewFile();
            BufferedReader br = new BufferedReader(new FileReader(userFile));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(user + ":")) {
                    JOptionPane.showMessageDialog(this, "User already exists.");
                    br.close();
                    return;
                }
            }
            br.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter(userFile, true));
            bw.write(user + ":" + pass + "\n");
            bw.close();
            JOptionPane.showMessageDialog(this, "Account created!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}

class TypingSpeedTester extends JFrame implements ActionListener {
    JTextArea displayText, inputText;
    JLabel timerLabel;
    JButton startBtn, submitBtn, exitBtn, leaderboardBtn, clearLeaderboardBtn, searchUserBtn, rankBtn;
    JComboBox<String> difficultyBox, fontBox, themeBox;
    JSpinner timeLimitSpinner;
    java.util.Timer timer;
    int timeLeft = 60;
    long startTime;
    String sampleText = "";
    String username;

    public TypingSpeedTester(String username) {
        this.username = username;

        setUndecorated(true);
        setTitle("Typing Speed Tester");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titleLabel = new JLabel("Typing Speed Tester - Welcome " + username, JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        displayText = new JTextArea();
        displayText.setLineWrap(true);
        displayText.setWrapStyleWord(true);
        displayText.setEditable(false);

        inputText = new JTextArea();
        inputText.setLineWrap(true);
        inputText.setWrapStyleWord(true);

        timerLabel = new JLabel("Time Left: 60s");
        startBtn = new JButton("Start");
        submitBtn = new JButton("Submit");
        exitBtn = new JButton("Exit");
        leaderboardBtn = new JButton("Leaderboard");
        clearLeaderboardBtn = new JButton("Clear Leaderboard");
        searchUserBtn = new JButton("Search My Scores");
        rankBtn = new JButton("Show My Rank");

        difficultyBox = new JComboBox<>(new String[]{"Easy", "Medium", "Hard"});
        fontBox = new JComboBox<>(new String[]{"Serif", "SansSerif", "Monospaced"});
        themeBox = new JComboBox<>(new String[]{"Light", "Dark"});
        timeLimitSpinner = new JSpinner(new SpinnerNumberModel(60, 10, 300, 10));

        startBtn.addActionListener(this);
        submitBtn.addActionListener(this);
        exitBtn.addActionListener(e -> System.exit(0));
        leaderboardBtn.addActionListener(e -> showLeaderboard());
        clearLeaderboardBtn.addActionListener(e -> clearLeaderboard());
        searchUserBtn.addActionListener(e -> showUserScores());
        rankBtn.addActionListener(e -> showRank());

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createTitledBorder("Type this"));
        top.add(new JScrollPane(displayText));

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Your Typing"));
        center.add(new JScrollPane(inputText));

        JPanel bottom = new JPanel();
        bottom.add(new JLabel("Difficulty:"));
        bottom.add(difficultyBox);
        bottom.add(new JLabel("Font:"));
        bottom.add(fontBox);
        bottom.add(new JLabel("Theme:"));
        bottom.add(themeBox);
        bottom.add(new JLabel("Time (s):"));
        bottom.add(timeLimitSpinner);
        bottom.add(timerLabel);
        bottom.add(startBtn);
        bottom.add(submitBtn);
        bottom.add(exitBtn);
        bottom.add(leaderboardBtn);
        bottom.add(clearLeaderboardBtn);
        bottom.add(searchUserBtn);
        bottom.add(rankBtn);

        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(top, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startBtn) {
            inputText.setText("");
            inputText.setEditable(true);
            timeLeft = (int) timeLimitSpinner.getValue();
            timerLabel.setText("Time Left: " + timeLeft + "s");
            sampleText = loadParagraph((String) difficultyBox.getSelectedItem());
            displayText.setText(sampleText);
            setFontAndTheme();
            startTime = System.currentTimeMillis();
            startTimer();
        }

        if (e.getSource() == submitBtn) {
            inputText.setEditable(false);
            if (timer != null) timer.cancel();

            String typed = inputText.getText();
            long end = System.currentTimeMillis();
            double time = (end - startTime) / 60000.0;

            int words = typed.trim().isEmpty() ? 0 : typed.trim().split("\\s+").length;
            int wpm = (int) (words / time);

            int correctChars = 0;
            for (int i = 0; i < Math.min(typed.length(), sampleText.length()); i++) {
                if (typed.charAt(i) == sampleText.charAt(i)) correctChars++;
            }

            double accuracy = (correctChars * 100.0) / sampleText.length();
            int errors = Math.abs(typed.length() - correctChars);

            saveScore(wpm, accuracy);

            JOptionPane.showMessageDialog(this,
                    "🎉 Congratulations " + username + ", you completed the test!\n" +
                            "WPM: " + wpm + "\nAccuracy: " + String.format("%.2f", accuracy) + "%\nErrors: " + errors);

            // 🔄 Clear text areas after test ends
            displayText.setText("");
            inputText.setText("");
        }
    }

    void setFontAndTheme() {
        String font = (String) fontBox.getSelectedItem();
        String theme = (String) themeBox.getSelectedItem();

        Font f = new Font(font, Font.PLAIN, 16);
        displayText.setFont(f);
        inputText.setFont(f);

        if ("Dark".equals(theme)) {
            displayText.setBackground(Color.DARK_GRAY);
            displayText.setForeground(Color.WHITE);
            inputText.setBackground(Color.BLACK);
            inputText.setForeground(Color.GREEN);
        } else {
            displayText.setBackground(Color.WHITE);
            displayText.setForeground(Color.BLACK);
            inputText.setBackground(Color.WHITE);
            inputText.setForeground(Color.BLACK);
        }
    }

    void startTimer() {
        timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                timeLeft--;
                timerLabel.setText("Time Left: " + timeLeft + "s");
                if (timeLeft <= 0) {
                    timer.cancel();
                    submitBtn.doClick();
                }
            }
        }, 1000, 1000);
    }

    String loadParagraph(String level) {
        String filename = "easy.txt";
        if (level.equals("Medium")) filename = "medium.txt";
        else if (level.equals("Hard")) filename = "hard.txt";

        try {
            java.util.List<String> lines = new java.util.ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) lines.add(line.trim());
            }
            br.close();
            if (!lines.isEmpty()) {
                Collections.shuffle(lines);
                return lines.get(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "The quick brown fox jumps over the lazy dog.";
    }

    void saveScore(int wpm, double accuracy) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scores.txt", true))) {
            writer.write(username + "," + wpm + "," + String.format("%.2f", accuracy) + "%\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void showLeaderboard() {
        try {
            java.util.List<String> lines = new java.util.ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader("scores.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
            lines.sort((a, b) -> {
                int wpmA = Integer.parseInt(a.split(",")[1]);
                int wpmB = Integer.parseInt(b.split(",")[1]);
                return Integer.compare(wpmB, wpmA);
            });
            StringBuilder leaderboard = new StringBuilder("Top Scores:\n\n");
            for (int i = 0; i < Math.min(10, lines.size()); i++) {
                leaderboard.append((i + 1) + ". " + lines.get(i) + "\n");
            }
            JOptionPane.showMessageDialog(this, leaderboard.toString());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading scores.");
        }
    }

    void clearLeaderboard() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear all leaderboard scores?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("scores.txt"))) {
                bw.write("");
                JOptionPane.showMessageDialog(this, "Leaderboard cleared!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error clearing leaderboard.");
            }
        }
    }

    void showUserScores() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("scores.txt"));
            StringBuilder result = new StringBuilder("Your Scores:\n\n");
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(username + ",")) {
                    result.append(line + "\n");
                    found = true;
                }
            }
            br.close();
            if (found)
                JOptionPane.showMessageDialog(this, result.toString());
            else
                JOptionPane.showMessageDialog(this, "No scores found for " + username);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading scores.");
        }
    }

    void showRank() {
        try {
            java.util.List<String> scores = new java.util.ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader("scores.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                scores.add(line);
            }
            br.close();

            scores.sort((a, b) -> {
                int wpmA = Integer.parseInt(a.split(",")[1]);
                int wpmB = Integer.parseInt(b.split(",")[1]);
                return Integer.compare(wpmB, wpmA);
            });

            int rank = 1;
            boolean found = false;
            for (String s : scores) {
                if (s.startsWith(username + ",")) {
                    JOptionPane.showMessageDialog(this,
                        "Your Rank: " + rank + "\nEntry: " + s);
                    found = true;
                    break;
                }
                rank++;
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No score found for ranking.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading scores.");
        }
    }
}
