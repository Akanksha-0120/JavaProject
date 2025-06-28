/**import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import javax.sound.sampled.*;
import java.net.*;
import javax.swing.Timer;

public class DigitalClockWithAlarms extends JFrame {
    private JLabel timeLabel, dateLabel, tempLabel;
    private JCheckBox darkModeToggle;
    private DefaultListModel<String> alarmListModel;
    private JList<String> alarmList;
    private java.util.List<String> alarmTimes = new ArrayList<>();
    private final String ALARM_FILE = "alarms.txt";
    private String activeAlarm = null;
    private JPanel topPanel, alarmPanel, bottomPanel;
    private Clip alarmClip;

    public DigitalClockWithAlarms() {
        setTitle("Digital Clock with Alarms");
        setSize(500, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        topPanel = new JPanel(new GridLayout(3, 1));
        timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Verdana", Font.BOLD, 36));
        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        tempLabel = new JLabel("Temperature: 28°C", SwingConstants.CENTER);
        tempLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        topPanel.add(timeLabel);
        topPanel.add(dateLabel);
        topPanel.add(tempLabel);

        alarmPanel = new JPanel(new FlowLayout());
        JTextField dateInput = new JTextField(8);
        dateInput.setToolTipText("Enter date (yyyy-MM-dd)");

        JTextField timeInput = new JTextField(5);
        timeInput.setToolTipText("Enter time (HH:mm)");

        JButton addAlarmBtn = new JButton("Add Alarm");

        addAlarmBtn.addActionListener(e -> {
            String date = dateInput.getText().trim();
            String time = timeInput.getText().trim();
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}") || !time.matches("\\d{2}:\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Enter valid date (yyyy-MM-dd) and time (HH:mm)");
                return;
            }
            String fullAlarm = date + " " + time;
            alarmTimes.add(fullAlarm);
            alarmListModel.addElement(fullAlarm);
            saveAlarms();
            JOptionPane.showMessageDialog(this, "Alarm set for " + fullAlarm);
            dateInput.setText("");
            timeInput.setText("");
        });

        alarmPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        alarmPanel.add(dateInput);
        alarmPanel.add(new JLabel("Time (HH:mm):"));
        alarmPanel.add(timeInput);
        alarmPanel.add(addAlarmBtn);

        alarmListModel = new DefaultListModel<>();
        alarmList = new JList<>(alarmListModel);
        JScrollPane alarmScroll = new JScrollPane(alarmList);

        JButton deleteAlarmBtn = new JButton("Delete Selected Alarm");
        deleteAlarmBtn.addActionListener(e -> {
            int index = alarmList.getSelectedIndex();
            if (index != -1) {
                alarmTimes.remove(index);
                alarmListModel.remove(index);
                saveAlarms();
            }
        });

        darkModeToggle = new JCheckBox("Dark Mode");
        darkModeToggle.addActionListener(e -> toggleTheme());

        bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(deleteAlarmBtn);
        bottomPanel.add(darkModeToggle);

        add(topPanel, BorderLayout.NORTH);
        add(alarmPanel, BorderLayout.CENTER);
        add(alarmScroll, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        loadAlarms();
        loadAlarmSound();

        Timer timer = new Timer(1000, e -> updateClock());
        timer.start();

        updateClock();
        setVisible(true);
    }

    private void updateClock() {
        Date now = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String currentTime = timeFormat.format(now);
        String fullCurrent = dateTimeFormat.format(now);
        timeLabel.setText(currentTime);
        dateLabel.setText(dateFormat.format(now));
        tempLabel.setText("Temperature: " + fetchLiveTemperature() + "°C");

        String shortCurrent = fullCurrent.substring(0, 16);
        if (alarmTimes.contains(shortCurrent)) {
            activeAlarm = shortCurrent;
            playAlarmSound();
            int choice = JOptionPane.showOptionDialog(this,
                    "⏰ Alarm! It's " + fullCurrent + "\nWhat do you want to do?",
                    "Alarm",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Snooze 5 min", "Turn Off", "Dismiss"},
                    "Dismiss");

            stopAlarmSound();
            if (choice == 0) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, 5);
                String newAlarm = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cal.getTime());
                alarmTimes.add(newAlarm);
                alarmListModel.addElement(newAlarm);
            }
            alarmTimes.remove(activeAlarm);
            alarmListModel.removeElement(activeAlarm);
            saveAlarms();
            activeAlarm = null;
        }
    }

    private String fetchLiveTemperature() {
        try {
            URL url = new URL("https://wttr.in/?format=%t");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String temp = in.readLine();
            return temp.replace("+", "").replace("°C", "").trim();
        } catch (Exception e) {
            return getRandomTemperature();
        }
    }

    private String getRandomTemperature() {
        Random rand = new Random();
        return String.valueOf(24 + rand.nextInt(10));
    }

    private void toggleTheme() {
        Color bg = darkModeToggle.isSelected() ? Color.DARK_GRAY : Color.WHITE;
        Color fg = darkModeToggle.isSelected() ? Color.WHITE : Color.BLACK;

        getContentPane().setBackground(bg);
        topPanel.setBackground(bg);
        alarmPanel.setBackground(bg);
        bottomPanel.setBackground(bg);

        for (Component c : topPanel.getComponents()) c.setForeground(fg);
        for (Component c : alarmPanel.getComponents()) {
            c.setBackground(bg);
            c.setForeground(fg);
        }
        for (Component c : bottomPanel.getComponents()) {
            c.setBackground(bg);
            c.setForeground(fg);
        }
        alarmList.setBackground(bg);
        alarmList.setForeground(fg);
    }

    private void saveAlarms() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ALARM_FILE))) {
            for (String alarm : alarmTimes) {
                writer.write(alarm);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAlarms() {
        File file = new File(ALARM_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                alarmTimes.add(line);
                alarmListModel.addElement(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAlarmSound() {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("alarm.wav"));
            alarmClip = AudioSystem.getClip();
            alarmClip.open(audioIn);
        } catch (Exception e) {
            System.err.println("Could not load alarm sound.");
        }
    }

    private void playAlarmSound() {
        if (alarmClip != null) {
            alarmClip.setFramePosition(0);
            alarmClip.start();
        }
    }

    private void stopAlarmSound() {
        if (alarmClip != null && alarmClip.isRunning()) {
            alarmClip.stop();
        }
    }

    public static void main(String[] args) {
        new DigitalClockWithAlarms();
    }
}**/
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import javax.swing.Timer;
import javax.sound.sampled.*;
import java.net.*;
public class DigitalClockWithAlarms extends JFrame {
    private JLabel timeLabel, dateLabel, tempLabel;
    private DefaultListModel<String> alarmListModel;
    private JList<String> alarmList;
    private java.util.List<String> alarmTimes = new ArrayList<>();
    private final String ALARM_FILE = "alarms.txt";
    private String activeAlarm = null;
    private JPanel centerPanel;
    private Clip alarmClip;
    private JCheckBox darkModeToggle;

    public DigitalClockWithAlarms() {
        setUndecorated(true);
        setTitle("Digital Clock");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Digital Clock", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Center Panel with Clock, Date, Temp
        centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));
        labelsPanel.setOpaque(false);

        timeLabel = new JLabel("", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Verdana", Font.BOLD, 36));
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        dateLabel = new JLabel("", SwingConstants.CENTER);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        dateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        tempLabel = new JLabel("Temperature: 28°C", SwingConstants.CENTER);
        tempLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        labelsPanel.add(timeLabel);
        labelsPanel.add(Box.createVerticalStrut(10));
        labelsPanel.add(dateLabel);
        labelsPanel.add(Box.createVerticalStrut(10));
        labelsPanel.add(tempLabel);

        centerPanel.add(labelsPanel, gbc);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Buttons
        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton setAlarmBtn = new JButton("Set Alarm");
        JButton exitBtn = new JButton("Exit");
        darkModeToggle = new JCheckBox("Dark Mode");

        bottomButtons.add(setAlarmBtn);
        bottomButtons.add(darkModeToggle);
        bottomButtons.add(exitBtn);

        exitBtn.addActionListener(e -> System.exit(0));
        darkModeToggle.addActionListener(e -> toggleTheme());
        setAlarmBtn.addActionListener(e -> openAlarmDialog());

        add(bottomButtons, BorderLayout.SOUTH);

        loadAlarms();
        loadAlarmSound();

        Timer timer = new Timer(1000, e -> updateClock());
        timer.start();

        new Timer(600_000, e -> tempLabel.setText("Temperature: " + fetchLiveTemperature() + "°C")).start();

        updateClock();
        setVisible(true);
    }
    private void openAlarmDialog() {
        JDialog dialog = new JDialog(this, "Set Alarm", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField dateInput = new JTextField();
        JTextField timeInput = new JTextField();
        inputPanel.add(new JLabel("Date (yyyy-MM-dd):"));
        inputPanel.add(dateInput);
        inputPanel.add(new JLabel("Time (HH:mm):"));
        inputPanel.add(timeInput);

        alarmListModel = new DefaultListModel<>();
        alarmList = new JList<>(alarmListModel);
        JScrollPane scrollPane = new JScrollPane(alarmList);

        JButton addAlarmBtn = new JButton("Add Alarm");
        JButton deleteAlarmBtn = new JButton("Delete Alarm");

        addAlarmBtn.addActionListener(e -> {
            String date = dateInput.getText().trim();
            String time = timeInput.getText().trim();
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}") || !time.matches("\\d{2}:\\d{2}")) {
                JOptionPane.showMessageDialog(dialog, "Enter valid date and time");
                return;
            }
            String fullAlarm = date + " " + time;
            alarmTimes.add(fullAlarm);
            alarmListModel.addElement(fullAlarm);
            saveAlarms();
            dateInput.setText("");
            timeInput.setText("");
        });

        deleteAlarmBtn.addActionListener(e -> {
            int index = alarmList.getSelectedIndex();
            if (index != -1) {
                alarmTimes.remove(index);
                alarmListModel.remove(index);
                saveAlarms();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addAlarmBtn);
        buttonPanel.add(deleteAlarmBtn);

        dialog.add(inputPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void updateClock() {
        Date now = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String currentTime = timeFormat.format(now);
        String fullCurrent = dateTimeFormat.format(now);
        timeLabel.setText(currentTime);
        dateLabel.setText(dateFormat.format(now));
        tempLabel.setText("Temperature: " + fetchLiveTemperature() + "°C");

        String shortCurrent = fullCurrent.substring(0, 16);
        if (alarmTimes.contains(shortCurrent)) {
            activeAlarm = shortCurrent;
            playAlarmSound();
            int choice = JOptionPane.showOptionDialog(this,
                    "⏰ Alarm! It's " + fullCurrent + "\nWhat do you want to do?",
                    "Alarm",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new String[]{"Snooze 5 min", "Turn Off", "Dismiss"},
                    "Dismiss");

            stopAlarmSound();
            if (choice == 0) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, 5);
                String newAlarm = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(cal.getTime());
                alarmTimes.add(newAlarm);
                alarmListModel.addElement(newAlarm);
            }
            alarmTimes.remove(activeAlarm);
            alarmListModel.removeElement(activeAlarm);
            saveAlarms();
            activeAlarm = null;
        }
    }

    private String fetchLiveTemperature() {
        try {
            URL url = new URL("https://wttr.in/?format=%t");
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String temp = in.readLine();
            return temp.replace("+", "").replace("°C", "").trim();
        } catch (Exception e) {
            return getRandomTemperature();
        }
    }

    private String getRandomTemperature() {
        Random rand = new Random();
        return String.valueOf(24 + rand.nextInt(10));
    }

    private void toggleTheme() {
        Color bg = darkModeToggle.isSelected() ? Color.DARK_GRAY : Color.WHITE;
        Color fg = darkModeToggle.isSelected() ? Color.WHITE : Color.BLACK;

        getContentPane().setBackground(bg);
        centerPanel.setBackground(bg);

        timeLabel.setForeground(fg);
        dateLabel.setForeground(fg);
        tempLabel.setForeground(fg);
        timeLabel.setBackground(bg);
        dateLabel.setBackground(bg);
        tempLabel.setBackground(bg);
    }

    private void saveAlarms() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ALARM_FILE))) {
            for (String alarm : alarmTimes) {
                writer.write(alarm);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAlarms() {
        File file = new File(ALARM_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                alarmTimes.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAlarmSound() {
        try {
            File alarmFile = new File("alarm.wav");
            if (!alarmFile.exists()) {
                System.err.println("alarm.wav not found. Using system beep.");
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(alarmFile);
            alarmClip = AudioSystem.getClip();
            alarmClip.open(audioIn);
        } catch (Exception e) {
            System.err.println("Could not load alarm sound: " + e.getMessage());
        }
    }

    private void playAlarmSound() {
        if (alarmClip != null) {
            alarmClip.setFramePosition(0);
            alarmClip.start();
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    private void stopAlarmSound() {
        if (alarmClip != null && alarmClip.isRunning()) {
            alarmClip.stop();
        }
    }

    public static void main(String[] args) {
        new DigitalClockWithAlarms();
    }
}