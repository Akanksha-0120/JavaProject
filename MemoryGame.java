/**import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class MemoryGame extends JFrame {
    private int rows = 4;
    private int cols = 4;
    private JButton[][] buttons = new JButton[rows][cols];
    private String[][] values = new String[rows][cols];
    private JButton firstSelected = null;
    private JButton secondSelected = null;
    private int moves = 0;
    private int matches = 0;
    private int bestMoves = Integer.MAX_VALUE;
    private JLabel scoreLabel, timerLabel;
    private JPanel gridPanel;
    private JButton restartBtn, exitBtn, easyBtn, hardBtn;
    private Timer gameTimer;
    private int timeElapsed = 0;

    public MemoryGame() {
        setTitle("Memory Game - Project 12");
        setSize(500, 650);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        scoreLabel = new JLabel("Moves: 0 | Matches: 0", SwingConstants.CENTER);
        timerLabel = new JLabel("Time: 0s", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        topPanel.add(scoreLabel);
        topPanel.add(timerLabel);
        add(topPanel, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(rows, cols, 5, 5));
        add(gridPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        restartBtn = new JButton("Restart");
        exitBtn = new JButton("Exit");

        JPanel difficultyPanel = new JPanel();
        easyBtn = new JButton("Easy");
        hardBtn = new JButton("Hard");
        difficultyPanel.add(easyBtn);
        difficultyPanel.add(hardBtn);

        restartBtn.addActionListener(e -> resetGame());
        exitBtn.addActionListener(e -> System.exit(0));
        easyBtn.addActionListener(e -> changeDifficulty(4));
        hardBtn.addActionListener(e -> changeDifficulty(6));

        bottomPanel.add(restartBtn, BorderLayout.WEST);
        bottomPanel.add(difficultyPanel, BorderLayout.CENTER);
        bottomPanel.add(exitBtn, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        initializeBoard();
        setVisible(true);
        startTimer();
    }

    private void startTimer() {
        timeElapsed = 0;
        gameTimer = new Timer(1000, e -> {
            timeElapsed++;
            timerLabel.setText("Time: " + timeElapsed + "s");
        });
        gameTimer.start();
    }

    private void stopTimer() {
        if (gameTimer != null) gameTimer.stop();
    }

    private void changeDifficulty(int gridSize) {
        stopTimer();
        dispose();
        SwingUtilities.invokeLater(() -> new MemoryGame(gridSize));
    }

    public MemoryGame(int gridSize) {
        super("Memory Game - Project 12");
        setSize(600, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        rows = gridSize;
        cols = gridSize;
        buttons = new JButton[rows][cols];
        values = new String[rows][cols];

        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        scoreLabel = new JLabel("Moves: 0 | Matches: 0", SwingConstants.CENTER);
        timerLabel = new JLabel("Time: 0s", SwingConstants.CENTER);
        topPanel.add(scoreLabel);
        topPanel.add(timerLabel);
        add(topPanel, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(gridSize, gridSize, 5, 5));
        add(gridPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        restartBtn = new JButton("Restart");
        exitBtn = new JButton("Exit");

        restartBtn.addActionListener(e -> resetGame());
        exitBtn.addActionListener(e -> System.exit(0));
        bottomPanel.add(restartBtn, BorderLayout.WEST);
        bottomPanel.add(exitBtn, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        startTimer();
        initializeBoard();
        setVisible(true);
    }

    private void initializeBoard() {
        List<String> pairList = new ArrayList<>();
        for (int i = 1; i <= (rows * cols) / 2; i++) {
            pairList.add(String.valueOf(i));
            pairList.add(String.valueOf(i));
        }
        Collections.shuffle(pairList);

        gridPanel.removeAll();

        int index = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton btn = new JButton();
                btn.setFont(new Font("Arial", Font.BOLD, 20));
                btn.setBackground(Color.LIGHT_GRAY);
                String val = pairList.get(index++);
                values[r][c] = val;
                btn.putClientProperty("value", val);

                final int row = r;
                final int col = c;

                btn.addActionListener(e -> handleButtonClick(btn, row, col));

                buttons[r][c] = btn;
                gridPanel.add(btn);
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();

        moves = 0;
        matches = 0;
        scoreLabel.setText("Moves: 0 | Matches: 0");
    }

    private void handleButtonClick(JButton btn, int row, int col) {
        if (firstSelected != null && secondSelected != null) return;
        if (btn.getText().length() > 0) return;

        btn.setText(values[row][col]);
        btn.setBackground(Color.WHITE);

        if (firstSelected == null) {
            firstSelected = btn;
        } else {
            secondSelected = btn;
            moves++;
            scoreLabel.setText("Moves: " + moves + " | Matches: " + matches);

            Timer t = new Timer(800, e -> checkMatch());
            t.setRepeats(false);
            t.start();
        }
    }

    private void checkMatch() {
        if (firstSelected.getText().equals(secondSelected.getText())) {
            firstSelected.setEnabled(false);
            secondSelected.setEnabled(false);
            matches++;
            scoreLabel.setText("Moves: " + moves + " | Matches: " + matches);
            if (matches == (rows * cols) / 2) {
                stopTimer();
                if (moves < bestMoves) {
                    bestMoves = moves;
                    JOptionPane.showMessageDialog(this, "🎉 New Best Score! You won in " + moves + " moves and " + timeElapsed + "s!");
                } else {
                    JOptionPane.showMessageDialog(this, "🎉 You matched all pairs in " + moves + " moves and " + timeElapsed + "s!");
                }
            }
        } else {
            firstSelected.setText("");
            secondSelected.setText("");
            firstSelected.setBackground(Color.LIGHT_GRAY);
            secondSelected.setBackground(Color.LIGHT_GRAY);
        }
        firstSelected = null;
        secondSelected = null;
    }

    private void resetGame() {
        stopTimer();
        timeElapsed = 0;
        startTimer();
        firstSelected = null;
        secondSelected = null;
        initializeBoard();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryGame::new);
    }
}**/

// MemoryGame.java - Project 12
/** 
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class MemoryGame extends JFrame {
    private int rows = 4;
    private int cols = 4;
    private JButton[][] buttons = new JButton[rows][cols];
    private String[][] values = new String[rows][cols];
    private JButton firstSelected = null;
    private JButton secondSelected = null;
    private int moves = 0;
    private int matches = 0;
    private int bestMoves = Integer.MAX_VALUE;
    private int bestTime = Integer.MAX_VALUE;
    private JLabel scoreLabel, timerLabel, bestLabel;
    private JPanel gridPanel;
    private JButton restartBtn, exitBtn, easyBtn, hardBtn;
    private JComboBox<String> modeSelector;
    private Timer gameTimer;
    private int timeElapsed = 0;
    private boolean useColors = false;
    private final String SAVE_FILE = "memory_game_stats.dat";

    public MemoryGame() {
        loadStats();
        setupUI();
        initializeBoard();
        setVisible(true);
        startTimer();
    }

    private void setupUI() {
        setTitle("Memory Game - Project 12");
        setSize(500, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        modeSelector = new JComboBox<>(new String[]{"Numbers", "Colors"});
        modeSelector.addActionListener(e -> {
            useColors = modeSelector.getSelectedIndex() == 1;
            resetGame();
        });
        topPanel.add(modeSelector);

        scoreLabel = new JLabel("Moves: 0 | Matches: 0", SwingConstants.CENTER);
        timerLabel = new JLabel("Time: 0s", SwingConstants.CENTER);
        bestLabel = new JLabel("Best - Moves: " + (bestMoves == Integer.MAX_VALUE ? "--" : bestMoves) +
                " | Time: " + (bestTime == Integer.MAX_VALUE ? "--" : bestTime + "s"), SwingConstants.CENTER);

        topPanel.add(scoreLabel);
        topPanel.add(timerLabel);
        topPanel.add(bestLabel);
        add(topPanel, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(rows, cols, 5, 5));
        add(gridPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        restartBtn = new JButton("Restart");
        exitBtn = new JButton("Exit");

        JPanel difficultyPanel = new JPanel();
        easyBtn = new JButton("Easy");
        hardBtn = new JButton("Hard");
        difficultyPanel.add(easyBtn);
        difficultyPanel.add(hardBtn);

        restartBtn.addActionListener(e -> {
            easyBtn.setVisible(true);
            hardBtn.setVisible(true);
            resetGame();
        });
        exitBtn.addActionListener(e -> {
            saveStats();
            System.exit(0);
        });
        easyBtn.addActionListener(e -> changeDifficulty(4));
        hardBtn.addActionListener(e -> changeDifficulty(6));

        bottomPanel.add(restartBtn, BorderLayout.WEST);
        bottomPanel.add(difficultyPanel, BorderLayout.CENTER);
        bottomPanel.add(exitBtn, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void startTimer() {
        timeElapsed = 0;
        gameTimer = new Timer(1000, e -> {
            timeElapsed++;
            timerLabel.setText("Time: " + timeElapsed + "s");
        });
        gameTimer.start();
    }

    private void stopTimer() {
        if (gameTimer != null) gameTimer.stop();
    }

    private void changeDifficulty(int gridSize) {
        stopTimer();
        dispose();
        SwingUtilities.invokeLater(() -> new MemoryGame(gridSize));
    }

    public MemoryGame(int gridSize) {
        this.rows = gridSize;
        this.cols = gridSize;
        buttons = new JButton[rows][cols];
        values = new String[rows][cols];
        loadStats();
        setupUI();
        initializeBoard();
        setVisible(true);
        startTimer();
    }

    private void initializeBoard() {
        List<String> pairList = new ArrayList<>();
        for (int i = 1; i <= (rows * cols) / 2; i++) {
            pairList.add(String.valueOf(i));
            pairList.add(String.valueOf(i));
        }
        Collections.shuffle(pairList);

        gridPanel.removeAll();

        int index = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton btn = new JButton();
                btn.setFont(new Font("Arial", Font.BOLD, 20));
                btn.setBackground(Color.LIGHT_GRAY);
                String val = pairList.get(index++);
                values[r][c] = val;
                btn.putClientProperty("value", val);

                final int row = r;
                final int col = c;

                btn.addActionListener(e -> handleButtonClick(btn, row, col));

                buttons[r][c] = btn;
                gridPanel.add(btn);
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();

        moves = 0;
        matches = 0;
        scoreLabel.setText("Moves: 0 | Matches: 0");
    }

    private void handleButtonClick(JButton btn, int row, int col) {
        if (firstSelected != null && secondSelected != null) return;
        if (btn.getText().length() > 0 || btn.getBackground() != Color.LIGHT_GRAY) return;

        String val = values[row][col];
        if (useColors) {
            btn.setBackground(getColor(val));
        } else {
            btn.setText(val);
        }

        if (firstSelected == null) {
            firstSelected = btn;
        } else {
            secondSelected = btn;
            moves++;
            scoreLabel.setText("Moves: " + moves + " | Matches: " + matches);

            Timer t = new Timer(800, e -> checkMatch());
            t.setRepeats(false);
            t.start();
        }
    }

    private void checkMatch() {
        String val1 = (String) firstSelected.getClientProperty("value");
        String val2 = (String) secondSelected.getClientProperty("value");
        if (val1.equals(val2)) {
            if (useColors) {
                // keep color background as is
            } else {
                firstSelected.setBackground(Color.WHITE);
                secondSelected.setBackground(Color.WHITE);
            }
            firstSelected.setEnabled(false);
            secondSelected.setEnabled(false);
            matches++;
            scoreLabel.setText("Moves: " + moves + " | Matches: " + matches);
            if (matches == (rows * cols) / 2) {
                stopTimer();
                if (moves < bestMoves) bestMoves = moves;
                if (timeElapsed < bestTime) bestTime = timeElapsed;
                saveStats();
                JOptionPane.showMessageDialog(this, "🎉 Game Over!\nMoves: " + moves + ", Time: " + timeElapsed + "s");
                bestLabel.setText("Best - Moves: " + bestMoves + " | Time: " + bestTime + "s");
            }
        } else {
            firstSelected.setText("");
            secondSelected.setText("");
            firstSelected.setBackground(Color.LIGHT_GRAY);
            secondSelected.setBackground(Color.LIGHT_GRAY);
        }
        firstSelected = null;
        secondSelected = null;
    }

    private void resetGame() {
        stopTimer();
        timeElapsed = 0;
        startTimer();
        firstSelected = null;
        secondSelected = null;
        initializeBoard();
    }

    private Color getColor(String val) {
        int hash = Integer.parseInt(val);
        switch (hash % 12) {
            case 0: return Color.RED;
            case 1: return Color.BLUE;
            case 2: return Color.GREEN;
            case 3: return Color.YELLOW;
            case 4: return Color.MAGENTA;
            case 5: return Color.ORANGE;
            case 6: return new Color(128, 0, 128); // Purple
            case 7: return new Color(0, 128, 128); // Teal
            case 8: return new Color(255, 105, 180); // Hot pink
            case 9: return new Color(165, 42, 42); // Brown
            case 10: return new Color(70, 130, 180); // Steel Blue
            default: return new Color(0, 255, 127); // Spring Green
        }
    }

    private void saveStats() {
        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeInt(bestMoves);
            out.writeInt(bestTime);
        } catch (IOException ignored) {}
    }

    private void loadStats() {
        try (DataInputStream in = new DataInputStream(new FileInputStream(SAVE_FILE))) {
            bestMoves = in.readInt();
            bestTime = in.readInt();
        } catch (IOException ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryGame::new);
    }
}**/
// MemoryGame.java - Project 12

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class MemoryGame extends JFrame {
    private int rows = 4;
    private int cols = 4;
    private JButton[][] buttons;
    private String[][] values;
    private JButton firstSelected = null;
    private JButton secondSelected = null;
    private int moves = 0;
    private int matches = 0;
    private int bestMoves = Integer.MAX_VALUE;
    private int bestTime = Integer.MAX_VALUE;
    private JLabel scoreLabel, timerLabel, bestLabel;
    private JPanel gridPanel;
    private JButton restartBtn, exitBtn, easyBtn, hardBtn, startBtn;
    private JComboBox<String> modeSelector;
    private Timer gameTimer;
    private int timeElapsed = 0;
    private boolean useColors = false;
    private boolean gameStarted = false;
    private final String SAVE_FILE = "memory_game_stats.dat";

    public MemoryGame() {
        loadStats();
        initUI();
        setVisible(true);
    }

    public MemoryGame(int gridSize) {
        this.rows = gridSize;
        this.cols = gridSize;
        loadStats();
        initUI();
        setVisible(true);
    }

    private void loadStats() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            bestMoves = ois.readInt();
            bestTime = ois.readInt();
        } catch (IOException ignored) {}
    }

    private void saveStats() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeInt(bestMoves);
            oos.writeInt(bestTime);
        } catch (IOException ignored) {}
    }

    private void initUI() {
        setTitle("Memory Game");
        setSize(500, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel
        JPanel topPanel = new JPanel(new GridLayout(3,1));
        modeSelector = new JComboBox<>(new String[]{"Numbers","Colors"});
        modeSelector.addActionListener(e -> {
            useColors = modeSelector.getSelectedIndex()==1;
            initializeBoard();
            gameStarted = false;
            stopTimer();
            // Start remains visible
        });
        scoreLabel = new JLabel("Moves: 0 | Matches: 0",SwingConstants.CENTER);
        timerLabel = new JLabel("Time: 0s",SwingConstants.CENTER);
        bestLabel = new JLabel("Best - Moves: " + (bestMoves==Integer.MAX_VALUE?"--":bestMoves) +
            " | Time: " + (bestTime==Integer.MAX_VALUE?"--":bestTime+"s"),SwingConstants.CENTER);
        topPanel.add(modeSelector);
        topPanel.add(scoreLabel);
        topPanel.add(timerLabel);
        topPanel.add(bestLabel);
        add(topPanel,BorderLayout.NORTH);

        // Grid Panel
        gridPanel = new JPanel(new GridLayout(rows,cols,5,5));
        add(gridPanel,BorderLayout.CENTER);

        // Bottom Panel
        restartBtn = new JButton("Restart");
        exitBtn = new JButton("Exit");
        easyBtn = new JButton("Easy");
        hardBtn = new JButton("Hard");
        startBtn = new JButton("Start");
        restartBtn.setVisible(false);
        exitBtn.setVisible(false);

        JPanel diffPanel = new JPanel();
        diffPanel.add(easyBtn);
        diffPanel.add(hardBtn);

        easyBtn.addActionListener(e -> restartWithSize(4));
        hardBtn.addActionListener(e -> restartWithSize(6));

        startBtn.addActionListener(e -> {
            gameStarted = true;
            startBtn.setVisible(false);
            restartBtn.setVisible(true);
            exitBtn.setVisible(true);
            initializeBoard();
            startTimer();
        });

        restartBtn.addActionListener(e -> {
            gameStarted = true;
            initializeBoard();
            startTimer();
        });
        exitBtn.addActionListener(e -> { saveStats(); System.exit(0); });

        JPanel bottom = new JPanel(new BorderLayout());
        JPanel controls = new JPanel(new BorderLayout());
        controllers:
        controls.add(restartBtn,BorderLayout.WEST);
        controls.add(diffPanel,BorderLayout.CENTER);
        controls.add(exitBtn,BorderLayout.EAST);

        bottom.add(startBtn,BorderLayout.NORTH);
        bottom.add(controls,BorderLayout.SOUTH);
        add(bottom,BorderLayout.SOUTH);

        initializeBoard();
    }

    private void restartWithSize(int size){
        stopTimer();
        dispose();
        SwingUtilities.invokeLater(() -> new MemoryGame(size));
    }

    private void startTimer(){
        timeElapsed=0; timerLabel.setText("Time: 0s");
        gameTimer=new Timer(1000,e->{ timeElapsed++; timerLabel.setText("Time: "+timeElapsed+"s"); });
        gameTimer.start();
    }
    private void stopTimer(){ if(gameTimer!=null) gameTimer.stop(); }

    private void initializeBoard(){
        buttons=new JButton[rows][cols]; values=new String[rows][cols];
        List<String> list=new ArrayList<>();
        for(int i=1;i<=rows*cols/2;i++){list.add(""+i);list.add(""+i);} Collections.shuffle(list);
        gridPanel.removeAll();
        int idx=0; moves=0; matches=0;
        scoreLabel.setText("Moves: 0 | Matches: 0");
        for(int r=0;r<rows;r++) for(int c=0;c<cols;c++){
            String v=list.get(idx); idx++;
            values[r][c]=v;
            JButton b=new JButton(); b.setFont(new Font("Arial",Font.BOLD,20)); b.setBackground(Color.LIGHT_GRAY);
            b.putClientProperty("value",v);
            int rr=r,cc=c;
            b.addActionListener(e-> handleTileClick(b,rr,cc));
            buttons[r][c]=b; gridPanel.add(b);
        }
        gridPanel.revalidate(); gridPanel.repaint();
    }

    private void handleTileClick(JButton b,int r,int c){
        if(!gameStarted||b.getText().length()>0||(firstSelected!=null&&secondSelected!=null))return;
        String v=values[r][c];
        if(useColors){ b.setBackground(getColorForValue(v)); } else b.setText(v);
        if(firstSelected==null) {
            firstSelected=b;
        } else {
            secondSelected=b; moves++; scoreLabel.setText("Moves: "+moves+" | Matches: "+matches);

            String v1=(String)firstSelected.getClientProperty("value");
            String v2=(String)secondSelected.getClientProperty("value");
            if(v1.equals(v2)){
                firstSelected.setEnabled(false);
                secondSelected.setEnabled(false);
                if (!useColors) {
                    firstSelected.setBackground(Color.WHITE);
                    secondSelected.setBackground(Color.WHITE);
                }
                matches++;
                if(matches==rows*cols/2){
                    stopTimer();
                    if(moves<bestMoves||timeElapsed<bestTime){
                        bestMoves=Math.min(bestMoves,moves);
                        bestTime=Math.min(bestTime,timeElapsed);
                        saveStats();
                    }
                    bestLabel.setText("Best - Moves: "+bestMoves+" | Time: "+bestTime+"s");
                    JOptionPane.showMessageDialog(this,"You won in "+moves+" moves & "+timeElapsed+"s!");
                }
                firstSelected=null;
                secondSelected=null;
            } else {
                JButton f=firstSelected, s=secondSelected;
                Timer t=new Timer(800, ev->{
                    if(useColors){
                        f.setBackground(Color.LIGHT_GRAY);
                        s.setBackground(Color.LIGHT_GRAY);
                    } else {
                        f.setText("");
                        s.setText("");
                    }
                    firstSelected=null;
                    secondSelected=null;
                    ((Timer)ev.getSource()).stop();
                });
                t.setRepeats(false);
                t.start();
            }
        }
    }


    private Color getColorForValue(String val){ 
        int v=Integer.parseInt(val); 
        switch(v%12){
            case 0:return Color.RED;
            case 1:return Color.BLUE;
            case 2:return Color.GREEN;
            case 3:return Color.YELLOW;
            case 4:return Color.MAGENTA;
            case 5:return Color.ORANGE;
            case 6:return new Color(128,0,128);
            case 7:return new Color(0,128,128);
            case 8:return new Color(255,105,180);
            case 9:return new Color(139,69,19);
            case 10:return new Color(70,130,180);
            default:return new Color(0,255,127);
        } 
    }

    public static void main(String[] args){ 
        SwingUtilities.invokeLater(MemoryGame::new); 
    }
}

