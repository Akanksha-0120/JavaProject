// Feature-rich Tic Tac Toe game in Java with Smart AI
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class TicTacToe extends JFrame implements ActionListener {
    private JButton[][] buttons = new JButton[3][3];
    private boolean playerXTurn = true;
    private boolean vsAI = false;
    private JLabel statusLabel;
    private int xScore = 0, oScore = 0;
    private JLabel scoreLabel;
    private JButton restartButton, exitButton;
    private JMenuItem switchModeItem, switchThemeItem;
    private boolean isDarkTheme = false;
    private JPanel controlPanel;

    public TicTacToe() {
        setTitle("Tic Tac Toe");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel boardPanel = new JPanel(new GridLayout(3, 3));
        Font font = new Font("Arial", Font.BOLD, 60);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setFont(font);
                buttons[i][j].addActionListener(this);
                boardPanel.add(buttons[i][j]);
            }
        }

        statusLabel = new JLabel("Your Turn (X)", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        scoreLabel = new JLabel("You: 0  |  Opponent: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));

        restartButton = new JButton("Restart");
        exitButton = new JButton("Exit");

        restartButton.addActionListener(e -> {
            if (vsAI) {
                playerXTurn = new Random().nextBoolean(); // Randomize starter in vsAI mode
            } else {
                playerXTurn = !playerXTurn; // alternate starter in 2-player mode
            }
            resetBoard();
        });
        exitButton.addActionListener(e -> System.exit(0));

        controlPanel = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();

        leftPanel.add(restartButton);
        rightPanel.add(exitButton);

        controlPanel.add(leftPanel, BorderLayout.WEST);
        controlPanel.add(scoreLabel, BorderLayout.CENTER);
        controlPanel.add(rightPanel, BorderLayout.EAST);

        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("Options");

        switchModeItem = new JMenuItem("Switch to Single Player");
        switchModeItem.addActionListener(e -> toggleMode());

        switchThemeItem = new JMenuItem("Toggle Theme");
        switchThemeItem.addActionListener(e -> toggleTheme());

        optionsMenu.add(switchModeItem);
        optionsMenu.add(switchThemeItem);
        menuBar.add(optionsMenu);

        setJMenuBar(menuBar);

        add(statusLabel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void toggleMode() {
        vsAI = !vsAI;
        switchModeItem.setText(vsAI ? "Switch to Two Player" : "Switch to Single Player");
        resetBoard();
    }

    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        Color bg = isDarkTheme ? Color.DARK_GRAY : Color.WHITE;
        Color fg = isDarkTheme ? Color.WHITE : Color.BLACK;
        getContentPane().setBackground(bg);
        statusLabel.setForeground(fg);
        scoreLabel.setForeground(fg);
        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                btn.setBackground(bg);
                btn.setForeground(fg);
            }
        }
    }

    private void resetBoard() {
        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                btn.setText("");
                btn.setEnabled(true);
            }
        }
        statusLabel.setText(playerXTurn ? "Your Turn (X)" : "Opponent's Turn (O)");

        // Trigger AI move immediately if it's their turn
        if (vsAI && !playerXTurn) {
            aiMoveSmart();
        }
    }
    

    private void checkWinner() {
        String winner = null;
        for (int i = 0; i < 3; i++) {
            if (!buttons[i][0].getText().isEmpty() &&
                buttons[i][0].getText().equals(buttons[i][1].getText()) &&
                buttons[i][1].getText().equals(buttons[i][2].getText())) {
                winner = buttons[i][0].getText();
            }
            if (!buttons[0][i].getText().isEmpty() &&
                buttons[0][i].getText().equals(buttons[1][i].getText()) &&
                buttons[1][i].getText().equals(buttons[2][i].getText())) {
                winner = buttons[0][i].getText();
            }
        }
        if (!buttons[0][0].getText().isEmpty() &&
            buttons[0][0].getText().equals(buttons[1][1].getText()) &&
            buttons[1][1].getText().equals(buttons[2][2].getText())) {
            winner = buttons[0][0].getText();
        }
        if (!buttons[0][2].getText().isEmpty() &&
            buttons[0][2].getText().equals(buttons[1][1].getText()) &&
            buttons[1][1].getText().equals(buttons[2][0].getText())) {
            winner = buttons[0][2].getText();
        }

        if (winner != null) {
            statusLabel.setText((winner.equals("X") ? "You" : "Opponent") + " Wins!");
            if (winner.equals("X")) xScore++;
            else oScore++;
            scoreLabel.setText("You: " + xScore + "  |  Opponent: " + oScore);
            disableBoard();
        } else if (isBoardFull()) {
            statusLabel.setText("It's a Draw!");
        } else if (vsAI && !playerXTurn) {
            aiMoveSmart();
        }
    }

    private boolean isBoardFull() {
        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                if (btn.getText().isEmpty()) return false;
            }
        }
        return true;
    }

    private void disableBoard() {
        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                btn.setEnabled(false);
            }
        }
    }

    private void aiMoveSmart() {
        // Try to win or block user
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    buttons[i][j].setText("O");
                    if (isWinningMove("O")) return;
                    buttons[i][j].setText("");
                }
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().isEmpty()) {
                    buttons[i][j].setText("X");
                    if (isWinningMove("X")) {
                        buttons[i][j].setText("O");
                        playerXTurn = true;
                        statusLabel.setText("Your Turn (X)");
                        checkWinner();
                        return;
                    }
                    buttons[i][j].setText("");
                }
            }
        }
        // Else random
        Random rand = new Random();
        int i, j;
        do {
            i = rand.nextInt(3);
            j = rand.nextInt(3);
        } while (!buttons[i][j].getText().isEmpty());

        buttons[i][j].setText("O");
        playerXTurn = true;
        statusLabel.setText("Your Turn (X)");
        checkWinner();
    }

    private boolean isWinningMove(String mark) {
        return (buttons[0][0].getText().equals(mark) && buttons[0][1].getText().equals(mark) && buttons[0][2].getText().equals(mark)) ||
               (buttons[1][0].getText().equals(mark) && buttons[1][1].getText().equals(mark) && buttons[1][2].getText().equals(mark)) ||
               (buttons[2][0].getText().equals(mark) && buttons[2][1].getText().equals(mark) && buttons[2][2].getText().equals(mark)) ||
               (buttons[0][0].getText().equals(mark) && buttons[1][0].getText().equals(mark) && buttons[2][0].getText().equals(mark)) ||
               (buttons[0][1].getText().equals(mark) && buttons[1][1].getText().equals(mark) && buttons[2][1].getText().equals(mark)) ||
               (buttons[0][2].getText().equals(mark) && buttons[1][2].getText().equals(mark) && buttons[2][2].getText().equals(mark)) ||
               (buttons[0][0].getText().equals(mark) && buttons[1][1].getText().equals(mark) && buttons[2][2].getText().equals(mark)) ||
               (buttons[0][2].getText().equals(mark) && buttons[1][1].getText().equals(mark) && buttons[2][0].getText().equals(mark));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btnClicked = (JButton) e.getSource();
        if (!btnClicked.getText().isEmpty()) return;

        if (playerXTurn) {
            btnClicked.setText("X");
            playerXTurn = false;
            statusLabel.setText("Opponent's Turn (O)");
        } else {
            btnClicked.setText("O");
            playerXTurn = true;
            statusLabel.setText("Your Turn (X)");
        }
        checkWinner();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TicTacToe());
    }
}