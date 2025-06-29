import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class PixelDrawingApp extends JFrame {
    private DrawingCanvas canvas;
    private Color selectedColor = Color.BLACK;

    public PixelDrawingApp() {
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        setSize(800, 800);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // === Top Panel with Title ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.DARK_GRAY);
        JLabel titleLabel = new JLabel("Pixel Drawing App", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        canvas = new DrawingCanvas(40, 40, 20);
        add(canvas, BorderLayout.CENTER);

        JPanel toolsPanel = new JPanel();
        JButton colorBtn = new JButton("Choose Color");
        JButton eraseBtn = new JButton("Eraser");
        JButton clearBtn = new JButton("Clear");
        JButton saveBtn = new JButton("Save as Image");
        JButton exitBtn = new JButton("Exit");

        String[] gridSizes = {"16x16", "32x32", "64x64"};
        JComboBox<String> gridSizeBox = new JComboBox<>(gridSizes);
        gridSizeBox.setSelectedIndex(1);

        colorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(null, "Select a color", selectedColor);
            if (c != null) {
                selectedColor = c;
                canvas.setSelectedColor(selectedColor);
            }
        });

        eraseBtn.addActionListener(e -> canvas.setSelectedColor(Color.WHITE));
        clearBtn.addActionListener(e -> canvas.clearCanvas());
        saveBtn.addActionListener(e -> canvas.saveAsImage());
        exitBtn.addActionListener(e -> System.exit(0));
        gridSizeBox.addActionListener(e -> {
            String choice = (String) gridSizeBox.getSelectedItem();
            int size = 20;
            if (choice != null) {
                switch (choice) {
                    case "16x16" -> canvas.updateGrid(16, 16, size);
                    case "32x32" -> canvas.updateGrid(32, 32, size);
                    case "64x64" -> canvas.updateGrid(64, 64, size);
                }
                pack();
            }
        });

        toolsPanel.add(colorBtn);
        toolsPanel.add(eraseBtn);
        toolsPanel.add(clearBtn);
        toolsPanel.add(saveBtn);
        toolsPanel.add(new JLabel("Grid Size: "));
        toolsPanel.add(gridSizeBox);
        toolsPanel.add(exitBtn);
        add(toolsPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PixelDrawingApp::new);
    }
}

// === DrawingCanvas.java ===
class DrawingCanvas extends JPanel implements MouseListener, MouseMotionListener {
    private int rows, cols, pixelSize;
    private Color[][] pixels;
    private Color selectedColor = Color.BLACK;

    public DrawingCanvas(int rows, int cols, int pixelSize) {
        updateGrid(rows, cols, pixelSize);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void updateGrid(int rows, int cols, int pixelSize) {
        this.rows = rows;
        this.cols = cols;
        this.pixelSize = pixelSize;
        this.pixels = new Color[rows][cols];
        setPreferredSize(new Dimension(cols * pixelSize, rows * pixelSize));
        repaint();
    }

    public void setSelectedColor(Color color) {
        this.selectedColor = color;
    }

    public void clearCanvas() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                pixels[r][c] = Color.WHITE;
            }
        }
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g.setColor(pixels[r][c] != null ? pixels[r][c] : Color.WHITE);
                g.fillRect(c * pixelSize, r * pixelSize, pixelSize, pixelSize);
                g.setColor(Color.GRAY);
                g.drawRect(c * pixelSize, r * pixelSize, pixelSize, pixelSize);
            }
        }
    }

    private void colorPixel(int x, int y) {
        int col = x / pixelSize;
        int row = y / pixelSize;
        if (row < rows && col < cols) {
            pixels[row][col] = selectedColor;
            repaint();
        }
    }

    public void saveAsImage() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        paint(g2);
        g2.dispose();

        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Image");
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                ImageIO.write(image, "png", new File(fileToSave.getAbsolutePath() + ".png"));
                JOptionPane.showMessageDialog(this, "Image saved successfully!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving image.");
        }
    }

    public void mouseClicked(MouseEvent e) {
        colorPixel(e.getX(), e.getY());
    }

    public void mouseDragged(MouseEvent e) {
        colorPixel(e.getX(), e.getY());
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
}

