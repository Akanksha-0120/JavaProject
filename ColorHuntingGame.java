// ColorHuntingGame.java - Project 13
// ColorHuntingGame.java - Project 13

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.io.*;

public class ColorHuntingGame extends JFrame {
    private JPanel gridPanel;
    private JLabel targetLabel, scoreLabel, timerLabel;
    private Color targetColor;
    private int score = 0;
    private int bestScore = 0;
    private final String SAVE_FILE = "color_hunting_best_score.dat";
    private int timeLeft = 60;
    private Timer gameTimer;
    private int gridSize = 4;
    private final Color[] colors = {
        Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
        Color.ORANGE, Color.MAGENTA, Color.PINK, Color.CYAN,Color.GRAY,Color.BLACK,Color.DARK_GRAY
    };

    public ColorHuntingGame() {
        loadBestScore();
        setTitle("Color Hunting Game - Project 13");
        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(1, 3));
        targetLabel = new JLabel("Target: ", SwingConstants.CENTER);
        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        timerLabel = new JLabel("Time: 60s", SwingConstants.CENTER);

        topPanel.add(targetLabel);
        topPanel.add(scoreLabel);
        topPanel.add(timerLabel);
        add(topPanel, BorderLayout.NORTH);

        gridPanel = new JPanel();
        add(gridPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        JButton easyBtn = new JButton("Easy");
        JButton mediumBtn = new JButton("Medium");
        JButton hardBtn = new JButton("Hard");
        JButton startBtn = new JButton("Start");
        JButton restartBtn = new JButton("Restart");
        JButton exitBtn = new JButton("Exit");
        restartBtn.setVisible(false);
        exitBtn.setVisible(false);

        easyBtn.addActionListener(e -> gridSize = 4);
        mediumBtn.addActionListener(e -> gridSize = 5);
        hardBtn.addActionListener(e -> gridSize = 6);

        startBtn.addActionListener(e -> {
            startGame();
            startBtn.setVisible(false);
            restartBtn.setVisible(true);
            exitBtn.setVisible(true);
        });

        bottomPanel.add(easyBtn);
        bottomPanel.add(mediumBtn);
        bottomPanel.add(hardBtn);
        bottomPanel.add(restartBtn);
        bottomPanel.add(startBtn);
        bottomPanel.add(exitBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        restartBtn.addActionListener(e -> {
            startGame();
        });

        exitBtn.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private void startGame() {
        score = 0;
        timeLeft = 60;
        updateScore();
        updateTimer();
        generateGrid();
        pickTargetColor();

        if (gameTimer != null) gameTimer.stop();
        gameTimer = new Timer(1000, e -> {
            timeLeft--;
            updateTimer();
            if (timeLeft <= 0) {
                gameTimer.stop();
                if (score > bestScore) {
                    bestScore = score;
                    saveBestScore();
                }
                JOptionPane.showMessageDialog(this, "Time's up! Your score: " + score + " | Best: " + bestScore);
            }
        });
        gameTimer.start();
    }

    private void updateScore() {
        scoreLabel.setText("Score: " + score + " | Best: " + bestScore);
    }

    private void saveBestScore() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeInt(bestScore);
        } catch (IOException ignored) {}
    }

    private void loadBestScore() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            bestScore = ois.readInt();
        } catch (IOException ignored) {}
    }

    private void updateTimer() {
        timerLabel.setText("Time: " + timeLeft + "s");
    }

    private void pickTargetColor() {
        Random rand = new Random();
        targetColor = colors[rand.nextInt(colors.length)];
        targetLabel.setText("Target: " + getColorName(targetColor));
        targetLabel.setForeground(targetColor);
    }

    private void generateGrid() {
        gridPanel.removeAll();
        gridPanel.setLayout(new GridLayout(gridSize, gridSize, 5, 5));
        Random rand = new Random();

        // Choose random index for guaranteed target tile
        int totalTiles = gridSize * gridSize;
        int targetIndex = rand.nextInt(totalTiles);

        for (int i = 0; i < totalTiles; i++) {
           Color assignedColor = (i == targetIndex) ? targetColor : colors[rand.nextInt(colors.length)];
           JButton tile = new JButton();
           tile.setBackground(assignedColor);
           tile.setOpaque(true);
           tile.setBorderPainted(false);

           tile.addActionListener(e -> {
           if (assignedColor.equals(targetColor)) {
             score++;
             updateScore();
             pickTargetColor();
             generateGrid();
            }
        });

    gridPanel.add(tile);
}

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private String getColorName(Color color) {
        if (color.equals(Color.RED)) return "Red";
        if (color.equals(Color.GREEN)) return "Green";
        if (color.equals(Color.BLUE)) return "Blue";
        if (color.equals(Color.YELLOW)) return "Yellow";
        if (color.equals(Color.ORANGE)) return "Orange";
        if (color.equals(Color.MAGENTA)) return "Magenta";
        if (color.equals(Color.PINK)) return "Pink";
        if (color.equals(Color.CYAN)) return "Cyan";
        if (color.equals(Color.BLACK)) return "Black";
        if (color.equals(Color.GRAY)) return "Gray";
        if (color.equals(Color.DARK_GRAY)) return "Dark_Gray";
        return "Unknown";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ColorHuntingGame::new);
    }
}
