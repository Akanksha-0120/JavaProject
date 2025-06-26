import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;

class GamePanel extends JPanel implements ActionListener {
    private final int TILE_SIZE = 25;
    private final int WIDTH = 600;
    private final int HEIGHT = 600;
    private final int TOTAL_TILES = (WIDTH * HEIGHT) / (TILE_SIZE * TILE_SIZE);

    private final int[] x = new int[TOTAL_TILES];
    private final int[] y = new int[TOTAL_TILES];

    private int bodyParts = 6;
    private int applesEaten;
    private int highScore = 0;
    private int appleX;
    private int appleY;
    private char direction = 'R';
    private boolean running = false;
    private boolean gameStarted = false;
    private Timer timer;
    private Random random;

    private JButton startButton, restartButton, exitButton;
    private JLabel scoreLabel;

    private final String SCORE_FILE = "snake_highscore.dat";

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT + 50));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        random = new Random();

        highScore = loadHighScore();

        setLayout(null);

        startButton = new JButton("Start");
        restartButton = new JButton("Restart");
        exitButton = new JButton("Exit");
        scoreLabel = new JLabel("", SwingConstants.CENTER);
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));

        startButton.setBounds((WIDTH - 100) / 2, HEIGHT / 2 + 50, 100, 30);
        restartButton.setBounds(20, HEIGHT + 10, 100, 30);
        scoreLabel.setBounds(140, HEIGHT + 10, 320, 30);
        exitButton.setBounds(460, HEIGHT + 10, 100, 30);

        add(startButton);
        add(restartButton);
        add(scoreLabel);
        add(exitButton);

        restartButton.setVisible(false);
        exitButton.setVisible(false);
        scoreLabel.setVisible(false);

        startButton.addActionListener(e -> {
            gameStarted = true;
            startButton.setVisible(false);
            startGame();
        });

        restartButton.addActionListener(e -> {
            if (applesEaten > highScore) {
                highScore = applesEaten;
                saveHighScore(highScore);
            }
            resetGame();
        });

        exitButton.addActionListener(e -> {
            if (applesEaten > highScore) {
                highScore = applesEaten;
                saveHighScore(highScore);
            }
            System.exit(0);
        });
    }

    private int loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) {
            return Integer.parseInt(reader.readLine());
        } catch (IOException | NumberFormatException e) {
            return 0;
        }
    }

    private void saveHighScore(int score) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORE_FILE))) {
            writer.write(String.valueOf(score));
        } catch (IOException ignored) {}
    }

    public void startGame() {
        applesEaten = 0;
        bodyParts = 6;
        direction = 'R';
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 100 - i * TILE_SIZE;
            y[i] = 100;
        }
        placeNewApple();
        running = true;
        scoreLabel.setText("Score: " + applesEaten + "   High Score: " + highScore);
        scoreLabel.setVisible(true);
        timer = new Timer(100, this);
        timer.start();
        requestFocusInWindow();
    }

    public void resetGame() {
        startButton.setVisible(false);
        restartButton.setVisible(false);
        exitButton.setVisible(false);
        scoreLabel.setVisible(false);
        startGame();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (!gameStarted) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Welcome to Snake Game", 130, HEIGHT / 2 - 20);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Click Start to Play", 210, HEIGHT / 2 + 10);
        } else if (running) {
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, TILE_SIZE, TILE_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                g.setColor(i == 0 ? Color.GREEN : new Color(45, 180, 0));
                g.fillRect(x[i], y[i], TILE_SIZE, TILE_SIZE);
            }
        } else {
            showGameOver(g);
        }
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U': y[0] -= TILE_SIZE; break;
            case 'D': y[0] += TILE_SIZE; break;
            case 'L': x[0] -= TILE_SIZE; break;
            case 'R': x[0] += TILE_SIZE; break;
        }
    }

    public void placeNewApple() {
        appleX = random.nextInt(WIDTH / TILE_SIZE) * TILE_SIZE;
        appleY = random.nextInt(HEIGHT / TILE_SIZE) * TILE_SIZE;
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            bodyParts++;
            applesEaten++;
            scoreLabel.setText("Score: " + applesEaten + "   High Score: " + highScore);
            placeNewApple();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                running = false;
                break;
            }
        }

        if (x[0] < 0 || x[0] >= WIDTH || y[0] < 0 || y[0] >= HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
            restartButton.setVisible(true);
            exitButton.setVisible(true);
        }
    }

    public void showGameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (WIDTH - metrics1.stringWidth("Game Over")) / 2, HEIGHT / 2);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
       
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') direction = 'L'; break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') direction = 'R'; break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') direction = 'U'; break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') direction = 'D'; break;
            }
        }
    }
}
public class SnakeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            GamePanel panel = new GamePanel();
            frame.add(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}