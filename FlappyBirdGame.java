import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class FlappyBirdGame extends JFrame {
    private GamePanel gamePanel;

    public FlappyBirdGame() {
        setTitle("Flappy Bird");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        gamePanel = new GamePanel();
        add(gamePanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FlappyBirdGame::new);
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener {
    private int birdY = 250, velocity = 0, gravity = 1, jump = -12;
    private int pipeX = 800, pipeGap = 200, pipeWidth = 80;
    private int pipeHeight = (int) (Math.random() * 250 + 100);
    private Timer timer;
    private int score = 0, highScore = 0;
    private boolean isRunning = false, gameOver = false;

    private JButton startBtn, restartBtn, exitBtn;
    private final String SAVE_FILE = "flappy_high_score.dat";

    public GamePanel() {
        setLayout(null);
        setBackground(new Color(135, 206, 235));
        setFocusable(true);
        addKeyListener(this);

        startBtn = new JButton("Start");
        restartBtn = new JButton("Restart");
        exitBtn = new JButton("Exit");

        startBtn.setBounds(340, 250, 120, 40);
        restartBtn.setBounds(100, 500, 100, 30);
        exitBtn.setBounds(600, 500, 100, 30);

        restartBtn.setVisible(false);
        exitBtn.setVisible(false);

        add(startBtn);
        add(restartBtn);
        add(exitBtn);

        startBtn.addActionListener(e -> startGame());
        restartBtn.addActionListener(e -> startGame());
        exitBtn.addActionListener(e -> System.exit(0));

        loadHighScore();

        timer = new Timer(20, this);
    }

    private void startGame() {
        birdY = 250;
        pipeX = 800;
        velocity = 0;
        score = 0;
        gameOver = false;
        isRunning = true;

        startBtn.setVisible(false);
        restartBtn.setVisible(false);
        exitBtn.setVisible(false);

        pipeHeight = (int) (Math.random() * 250 + 100);
        timer.start();
        requestFocus();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        velocity += gravity;
        birdY += velocity;
        pipeX -= 5;

        // Reset pipe
        if (pipeX < -pipeWidth) {
            pipeX = 800;
            pipeHeight = (int) (Math.random() * 250 + 100);
            score++;
            if (score > highScore) {
                highScore = score;
                saveHighScore();
            }
        }

        // Collision Detection
        Rectangle bird = new Rectangle(100, birdY, 30, 30);
        Rectangle topPipe = new Rectangle(pipeX, 0, pipeWidth, pipeHeight);
        Rectangle bottomPipe = new Rectangle(pipeX, pipeHeight + pipeGap, pipeWidth, getHeight() - pipeHeight - pipeGap);

        if (bird.intersects(topPipe) || bird.intersects(bottomPipe) || birdY > getHeight() || birdY < 0) {
            timer.stop();
            isRunning = false;
            gameOver = true;
            restartBtn.setVisible(true);
            exitBtn.setVisible(true);
        }

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Bird
        g.setColor(Color.YELLOW);
        g.fillOval(100, birdY, 30, 30);

        // Pipes
        g.setColor(Color.GREEN);
        g.fillRect(pipeX, 0, pipeWidth, pipeHeight);
        g.fillRect(pipeX, pipeHeight + pipeGap, pipeWidth, getHeight());

        // Score
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 30, 40);
        g.drawString("High Score: " + highScore, 600, 40);

        if (!isRunning && !gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Press Start to Begin!", 240, 200);
        }

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Game Over!", 300, 240);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isRunning && e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocity = jump;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    private void saveHighScore() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeInt(highScore);
        } catch (IOException ignored) {}
    }

    private void loadHighScore() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            highScore = ois.readInt();
        } catch (IOException ignored) {}
    }
}
