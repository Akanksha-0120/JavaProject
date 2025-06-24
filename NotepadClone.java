/**import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class NotepadClone extends JFrame {
    private JTabbedPane tabbedPane;
    private JFileChooser fileChooser;
    private JLabel statusBar;
    private JTextField findField, replaceField;

    public NotepadClone() {
        setTitle("Notepad Clone");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();
        add(tabbedPane);

        createMenuBar();
        createStatusBar();

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        addNewTab();
        setVisible(true);
    }

    private void addNewTab() {
        JTextPane textPane = new JTextPane();
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        JScrollPane scroll = new JScrollPane(textPane);
        tabbedPane.addTab("Untitled", scroll);
        tabbedPane.setSelectedComponent(scroll);

        textPane.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateWordCount(textPane);
            }
            public void removeUpdate(DocumentEvent e) {
                updateWordCount(textPane);
            }
            public void changedUpdate(DocumentEvent e) {}
        });

        textPane.addCaretListener(e -> updateWordCount(textPane));
    }

    private JTextPane getCurrentTextPane() {
        JScrollPane scrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
        return (JTextPane) scrollPane.getViewport().getView();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newFile = new JMenuItem("New Tab");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        JMenuItem exit = new JMenuItem("Exit");

        newFile.addActionListener(e -> addNewTab());
        openFile.addActionListener(e -> openFile());
        saveFile.addActionListener(e -> saveFile());
        exit.addActionListener(e -> System.exit(0));

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(exit);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem findItem = new JMenuItem("Find");
        JMenuItem replaceItem = new JMenuItem("Replace");

        findItem.addActionListener(e -> findText());
        replaceItem.addActionListener(e -> replaceText());
        editMenu.add(findItem);
        editMenu.add(replaceItem);

        JMenu formatMenu = new JMenu("Format");
        JMenuItem fontItem = new JMenuItem("Font");
        fontItem.addActionListener(e -> changeFont());
        formatMenu.add(fontItem);

        JMenu themeMenu = new JMenu("Theme");
        JMenuItem lightTheme = new JMenuItem("Light");
        JMenuItem darkTheme = new JMenuItem("Dark");

        lightTheme.addActionListener(e -> setTheme(Color.WHITE, Color.BLACK));
        darkTheme.addActionListener(e -> setTheme(new Color(45, 45, 45), Color.WHITE));

        themeMenu.add(lightTheme);
        themeMenu.add(darkTheme);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        menuBar.add(themeMenu);

        setJMenuBar(menuBar);
    }

    private void createStatusBar() {
        statusBar = new JLabel("Words: 0");
        add(statusBar, BorderLayout.SOUTH);
    }

    private void updateWordCount(JTextPane textPane) {
        String text = textPane.getText().trim();
        int words = text.isEmpty() ? 0 : text.split("\\s+").length;
        statusBar.setText("Words: " + words);
    }

    private void openFile() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                JTextPane textPane = new JTextPane();
                textPane.read(reader, null);
                JScrollPane scroll = new JScrollPane(textPane);
                tabbedPane.addTab(file.getName(), scroll);
                tabbedPane.setSelectedComponent(scroll);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage());
            }
        }
    }

    private void saveFile() {
        JTextPane textPane = getCurrentTextPane();
        if (textPane == null) return;

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(textPane.getText());
                tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), file.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
            }
        }
    }

    private void findText() {
        String find = JOptionPane.showInputDialog(this, "Enter text to find:");
        if (find != null && !find.isEmpty()) {
            JTextPane textPane = getCurrentTextPane();
            String content = textPane.getText();
            int index = content.indexOf(find);
            if (index >= 0) {
                textPane.setCaretPosition(index);
                textPane.moveCaretPosition(index + find.length());
            } else {
                JOptionPane.showMessageDialog(this, "Text not found.");
            }
        }
    }

    private void replaceText() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Find:"));
        findField = new JTextField();
        panel.add(findField);
        panel.add(new JLabel("Replace with:"));
        replaceField = new JTextField();
        panel.add(replaceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Find and Replace", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String find = findField.getText();
            String replace = replaceField.getText();
            JTextPane textPane = getCurrentTextPane();
            String content = textPane.getText().replace(find, replace);
            textPane.setText(content);
        }
    }

    private void changeFont() {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        String selectedFont = (String) JOptionPane.showInputDialog(this, "Choose Font:", "Font", JOptionPane.PLAIN_MESSAGE, null, fonts, "Monospaced");
        if (selectedFont != null) {
            getCurrentTextPane().setFont(new Font(selectedFont, Font.PLAIN, 14));
        }
    }

    private void setTheme(Color bg, Color fg) {
        Component component = getCurrentTextPane();
        component.setBackground(bg);
        component.setForeground(fg);
        statusBar.setBackground(bg);
        statusBar.setForeground(fg);
    }

    public static void main(String[] args) {
        new NotepadClone();
    }
}**/
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class NotepadClone extends JFrame {
    private JTabbedPane tabbedPane;
    private JFileChooser fileChooser;
    private JLabel statusBar;
    private JTextField findField, replaceField;
    private JPanel statusPanel;

    public NotepadClone() {
        setTitle("Notepad Clone");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();
        add(tabbedPane);

        createMenuBar();
        createStatusBarWithButtons();  // Combines word count + buttons

        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));

        addNewTab();
        setVisible(true);
    }

    private void addNewTab() {
        JTextPane textPane = new JTextPane();
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        JScrollPane scroll = new JScrollPane(textPane);
        tabbedPane.addTab("Untitled", scroll);
        tabbedPane.setSelectedComponent(scroll);

        textPane.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateWordCount(textPane);
            }
            public void removeUpdate(DocumentEvent e) {
                updateWordCount(textPane);
            }
            public void changedUpdate(DocumentEvent e) {}
        });

        textPane.addCaretListener(e -> updateWordCount(textPane));
    }

    private JTextPane getCurrentTextPane() {
        JScrollPane scrollPane = (JScrollPane) tabbedPane.getSelectedComponent();
        if (scrollPane == null) return null;
        return (JTextPane) scrollPane.getViewport().getView();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newFile = new JMenuItem("New Tab");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        JMenuItem exit = new JMenuItem("Exit");

        newFile.addActionListener(e -> addNewTab());
        openFile.addActionListener(e -> openFile());
        saveFile.addActionListener(e -> saveFile());
        exit.addActionListener(e -> System.exit(0));

        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(exit);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem findItem = new JMenuItem("Find");
        JMenuItem replaceItem = new JMenuItem("Replace");

        findItem.addActionListener(e -> findText());
        replaceItem.addActionListener(e -> replaceText());

        editMenu.add(findItem);
        editMenu.add(replaceItem);

        JMenu formatMenu = new JMenu("Format");
        JMenuItem fontItem = new JMenuItem("Font");

        fontItem.addActionListener(e -> changeFont());
        formatMenu.add(fontItem);

        JMenu themeMenu = new JMenu("Theme");
        JMenuItem lightTheme = new JMenuItem("Light");
        JMenuItem darkTheme = new JMenuItem("Dark");

        lightTheme.addActionListener(e -> setTheme(new Color(245, 245, 245), Color.BLACK));
        darkTheme.addActionListener(e -> setTheme(new Color(30, 30, 30), new Color(230, 230, 230)));

        themeMenu.add(lightTheme);
        themeMenu.add(darkTheme);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(formatMenu);
        menuBar.add(themeMenu);

        setJMenuBar(menuBar);
    }

    private void createStatusBarWithButtons() {
        statusPanel = new JPanel(new BorderLayout());

        // Word counter
        statusBar = new JLabel("Words: 0");
        statusPanel.add(statusBar, BorderLayout.WEST);

        // Buttons on the right
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backspaceButton = new JButton("Backspace");
        JButton exitButton = new JButton("Exit");

        backspaceButton.addActionListener(e -> {
            JTextPane textPane = getCurrentTextPane();
            if (textPane != null) {
                String text = textPane.getText();
                if (!text.isEmpty()) {
                    textPane.setText(text.substring(0, text.length() - 1));
                }
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(backspaceButton);
        buttonPanel.add(exitButton);
        statusPanel.add(buttonPanel, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    private void updateWordCount(JTextPane textPane) {
        String text = textPane.getText().trim();
        int words = text.isEmpty() ? 0 : text.split("\\s+").length;
        statusBar.setText("Words: " + words);
    }

    private void openFile() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                JTextPane textPane = new JTextPane();
                textPane.read(reader, null);
                textPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
                JScrollPane scroll = new JScrollPane(textPane);
                tabbedPane.addTab(file.getName(), scroll);
                tabbedPane.setSelectedComponent(scroll);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error opening file: " + ex.getMessage());
            }
        }
    }

    private void saveFile() {
        JTextPane textPane = getCurrentTextPane();
        if (textPane == null) return;

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(textPane.getText());
                tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), file.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
            }
        }
    }

    private void findText() {
        String find = JOptionPane.showInputDialog(this, "Enter text to find:");
        if (find != null && !find.isEmpty()) {
            JTextPane textPane = getCurrentTextPane();
            String content = textPane.getText();
            int index = content.indexOf(find);
            if (index >= 0) {
                textPane.setCaretPosition(index);
                textPane.moveCaretPosition(index + find.length());
            } else {
                JOptionPane.showMessageDialog(this, "Text not found.");
            }
        }
    }

    private void replaceText() {
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Find:"));
        findField = new JTextField();
        panel.add(findField);
        panel.add(new JLabel("Replace with:"));
        replaceField = new JTextField();
        panel.add(replaceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Find and Replace", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String find = findField.getText();
            String replace = replaceField.getText();
            JTextPane textPane = getCurrentTextPane();
            String content = textPane.getText().replace(find, replace);
            textPane.setText(content);
        }
    }

    private void changeFont() {
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        String selectedFont = (String) JOptionPane.showInputDialog(this, "Choose Font:", "Font", JOptionPane.PLAIN_MESSAGE, null, fonts, "Monospaced");
        if (selectedFont != null) {
            getCurrentTextPane().setFont(new Font(selectedFont, Font.PLAIN, 14));
        }
    }

    private void setTheme(Color bg, Color fg) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            JScrollPane scrollPane = (JScrollPane) tabbedPane.getComponentAt(i);
            JTextPane textPane = (JTextPane) scrollPane.getViewport().getView();
            textPane.setBackground(bg);
            textPane.setForeground(fg);
            textPane.setCaretColor(fg);
        }

        statusBar.setBackground(bg);
        statusBar.setForeground(fg);
        statusPanel.setBackground(bg);
        getContentPane().setBackground(bg);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(NotepadClone::new);
    }
}