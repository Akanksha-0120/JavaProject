
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GamePlay extends JPanel implements KeyListener, ActionListener {
    private boolean play = false;
    private int score = 0;
    private int highScore = 0;
    private int totalBricks = 28;

    private Timer timer;
    private int delay = 8;

    private int playerX = 310;

    private int ballPosX = 120, ballPosY = 350;
    private int ballDirX = -1, ballDirY = -2;

    private int ball2PosX = 160, ball2PosY = 320;
    private int ball2DirX = 1, ball2DirY = -2;

    private MapGenerator map;
    private JButton exitBtn, startBtn, restartBtn;
    private JPanel buttonPanel;
    private GamePanel gamePanel;

    public GamePlay() {
        setLayout(new BorderLayout());
        map = new MapGenerator(4, 7);

        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(692, 600));
        add(gamePanel, BorderLayout.CENTER);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        startBtn = new JButton("Start");
        restartBtn = new JButton("Restart");
        exitBtn = new JButton("Exit");

        startBtn.addActionListener(e -> {
            if (!play) {
                play = true;
                updateButtons();
                requestFocusInWindow();
            }
        });
        restartBtn.addActionListener(e -> {
            restartGame();
            updateButtons();
        });
        exitBtn.addActionListener(e -> System.exit(0));

        add(buttonPanel, BorderLayout.SOUTH);
        updateButtons();

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        timer = new Timer(delay, this);
        timer.start();
    }

    private void updateButtons() {
        buttonPanel.removeAll();
        if (!play && score == 0) {
            buttonPanel.add(startBtn);
        } else if (!play && (ballPosY > 570 && ball2PosY > 570 || totalBricks <= 0)) {
            buttonPanel.add(restartBtn);
            buttonPanel.add(exitBtn);
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private class GamePanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.black);
            g.fillRect(1, 1, 692, 592);

            map.draw((Graphics2D) g);

            g.setColor(Color.yellow);
            g.fillRect(0, 0, 3, 592);
            g.fillRect(0, 0, 692, 3);
            g.fillRect(691, 0, 3, 592);

            g.setColor(Color.white);
            g.setFont(new Font("serif", Font.BOLD, 18));
            g.drawString("Score: " + score, 530, 30);
            g.drawString("High Score: " + highScore, 50, 30);

            if (!play && score == 0) {
                g.setColor(Color.white);
                g.setFont(new Font("serif", Font.BOLD, 25));
                g.drawString("Press Enter or Start Button to Begin", 180, 300);
            }

            g.setColor(new Color(0, 153, 255));
            g.fillRect(playerX, 550, 100, 8);

            g.setColor(Color.yellow);
            g.fillOval(ballPosX, ballPosY, 20, 20);

            g.setColor(Color.cyan);
            g.fillOval(ball2PosX, ball2PosY, 20, 20);

            if (totalBricks <= 0) {
                play = false;
                ballDirX = ballDirY = ball2DirX = ball2DirY = 0;
                g.setColor(Color.green);
                g.setFont(new Font("serif", Font.BOLD, 30));
                g.drawString("You Won!", 260, 300);
                g.setFont(new Font("serif", Font.BOLD, 20));
                g.drawString("Press Enter to Restart", 230, 350);
                updateButtons();
            }

            if (ballPosY > 570 && ball2PosY > 570) {
                play = false;
                if (score > highScore) highScore = score;
                g.setColor(Color.red);
                g.setFont(new Font("serif", Font.BOLD, 30));
                g.drawString("Game Over! Score: " + score, 180, 300);
                g.setFont(new Font("serif", Font.BOLD, 20));
                g.drawString("Press Enter to Restart", 230, 350);
                updateButtons();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timer.start();
        if (play) {
            Rectangle paddle = new Rectangle(playerX, 550, 100, 8);
            Rectangle ball = new Rectangle(ballPosX, ballPosY, 20, 20);
            Rectangle ball2 = new Rectangle(ball2PosX, ball2PosY, 20, 20);

            if (ball.intersects(paddle)) ballDirY = -ballDirY;
            if (ball2.intersects(paddle)) ball2DirY = -ball2DirY;

            updateBall(ballPosX, ballPosY, ballDirX, ballDirY, true);
            updateBall(ball2PosX, ball2PosY, ball2DirX, ball2DirY, false);

            ballPosX += ballDirX; ballPosY += ballDirY;
            ball2PosX += ball2DirX; ball2PosY += ball2DirY;

            if (ballPosX < 0 || ballPosX > 670) ballDirX = -ballDirX;
            if (ballPosY < 0) ballDirY = -ballDirY;
            if (ball2PosX < 0 || ball2PosX > 670) ball2DirX = -ball2DirX;
            if (ball2PosY < 0) ball2DirY = -ball2DirY;
        }
        gamePanel.repaint();
    }

    private void updateBall(int x, int y, int dirX, int dirY, boolean isFirstBall) {
        Rectangle ballRect = new Rectangle(x, y, 20, 20);
        for (int i = 0; i < map.map.length; i++) {
            for (int j = 0; j < map.map[0].length; j++) {
                if (map.map[i][j] > 0) {
                    int bx = j * map.brickWidth + 80;
                    int by = i * map.brickHeight + 50;
                    Rectangle brickRect = new Rectangle(bx, by, map.brickWidth, map.brickHeight);

                    if (ballRect.intersects(brickRect)) {
                        map.setBrickValue(0, i, j);
                        totalBricks--;
                        score += 5;
                        if (x + 19 <= bx || x + 1 >= bx + map.brickWidth) {
                            if (isFirstBall) ballDirX = -ballDirX;
                            else ball2DirX = -ball2DirX;
                        } else {
                            if (isFirstBall) ballDirY = -ballDirY;
                            else ball2DirY = -ball2DirY;
                        }
                        return;
                    }
                }
            }
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) moveRight();
        if (e.getKeyCode() == KeyEvent.VK_LEFT) moveLeft();
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                updateButtons();
            } else {
                restartGame();
                updateButtons();
            }
        }
    }

    private void restartGame() {
        play = false;
        score = 0;
        totalBricks = 28;
        ballPosX = 120; ballPosY = 350; ballDirX = -1; ballDirY = -2;
        ball2PosX = 160; ball2PosY = 320; ball2DirX = 1; ball2DirY = -2;
        playerX = 310;
        map = new MapGenerator(4, 7);
        gamePanel.repaint();
    }

    private void moveRight() {
        play = true;
        playerX = Math.min(playerX + 20, 600);
        gamePanel.repaint();
    }

    private void moveLeft() {
        play = true;
        playerX = Math.max(playerX - 20, 10);
        gamePanel.repaint();
    }
}

class MapGenerator {
    public int[][] map;
    public int brickWidth;
    public int brickHeight;

    public MapGenerator(int row, int col) {
        map = new int[row][col];
        for (int[] rowArr : map) java.util.Arrays.fill(rowArr, 1);
        brickWidth = 540 / col;
        brickHeight = 150 / row;
    }

    public void draw(Graphics2D g) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    g.setColor(Color.white);
                    g.fillRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                    g.setStroke(new BasicStroke(3));
                    g.setColor(Color.black);
                    g.drawRect(j * brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
                }
            }
        }
    }

    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }
}

public class BrickBreaker {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        GamePlay gameplay = new GamePlay();
        frame.setBounds(10, 10, 700, 600);
        frame.setTitle("Brick Breaker Game");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(gameplay);
        frame.setVisible(true);
    }
}
