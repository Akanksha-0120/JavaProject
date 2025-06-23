import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Date;

public class ToDoListApp extends JFrame implements ActionListener {
    DefaultListModel<JCheckBox> taskListModel;
    JList<JCheckBox> taskList;
    JTextField taskInput;
    JTextField dueDateInput;
    JButton addBtn, deleteBtn, saveBtn, taskLabelBtn, dueDatePickerBtn;
    String task;
    final String FILE_NAME = "tasks.txt";
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ToDoListApp() {
        setTitle("To-Do List");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        taskLabelBtn = new JButton("Task:");
        taskLabelBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        taskLabelBtn.setFocusable(false);

        taskInput = new JTextField();
        taskInput.setFont(new Font("SansSerif", Font.PLAIN, 16));
        taskInput.setForeground(Color.BLACK);
        taskInput.setToolTipText("Type your task here");

        dueDateInput = new JTextField();
        dueDateInput.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dueDateInput.setForeground(Color.DARK_GRAY);
        dueDateInput.setToolTipText("Enter due date (optional)");
        dueDateInput.setText("Due: yyyy-MM-dd HH:mm");

        dueDatePickerBtn = new JButton("Pick Date");
        dueDatePickerBtn.addActionListener(e -> {
            JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
            dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd HH:mm"));
            int option = JOptionPane.showOptionDialog(this, dateSpinner, "Select Due Date",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
            if (option == JOptionPane.OK_OPTION) {
                Date selectedDate = (Date) dateSpinner.getValue();
                dueDateInput.setText(formatter.format(selectedDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()));
            }
        });
        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JPanel labelPanel = new JPanel(new BorderLayout(5, 5));
        labelPanel.add(taskLabelBtn, BorderLayout.WEST);
        labelPanel.add(taskInput, BorderLayout.CENTER);
        inputPanel.add(labelPanel);

        JPanel duePanel = new JPanel(new BorderLayout(5, 5));
        duePanel.add(dueDateInput, BorderLayout.CENTER);
        duePanel.add(dueDatePickerBtn, BorderLayout.EAST);
        inputPanel.add(duePanel);

        add(inputPanel, BorderLayout.NORTH);

        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setCellRenderer(new CheckboxListRenderer());
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int index = taskList.locationToIndex(e.getPoint());
                if (index != -1) {
                    JCheckBox checkbox = taskListModel.get(index);
                    checkbox.setSelected(!checkbox.isSelected());
                    taskList.repaint();
                }
            }
        });

        add(new JScrollPane(taskList), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 10));

        addBtn = new JButton("Add Task");
        deleteBtn = new JButton("Delete Selected");
        saveBtn = new JButton("Save Tasks");

        addBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        deleteBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveBtn.setFont(new Font("SansSerif", Font.BOLD, 14));

        addBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        saveBtn.addActionListener(this);

        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(saveBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        loadTasks();
        setVisible(true);
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBtn) {
            task = taskInput.getText().trim();
            String due = dueDateInput.getText().trim();
            if (!task.isEmpty()) {
                String timestamp = LocalDateTime.now().format(formatter);
                String taskText = task + " (Created: " + timestamp + ")";
                if (!due.isEmpty() && !due.equals("Due: yyyy-MM-dd HH:mm")) {
                    taskText += " | Due: " + due;
                    try {
                        LocalDateTime dueTime = LocalDateTime.parse(due, formatter);
                        scheduleReminder(task, dueTime);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Invalid date format. Use yyyy-MM-dd HH:mm");
                    }
                }
                JCheckBox newTask = new JCheckBox(taskText);
                newTask.setFont(new Font("SansSerif", Font.PLAIN, 16));
                taskListModel.addElement(newTask);
                taskInput.setText("");
                dueDateInput.setText("Due: yyyy-MM-dd HH:mm");
                JOptionPane.showMessageDialog(this, "Task added successfully!");
            }
        } else if (e.getSource() == deleteBtn) {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                taskListModel.remove(selectedIndex);
                JOptionPane.showMessageDialog(this, "Task deleted successfully!");
            }
        } else if (e.getSource() == saveBtn) {
            saveTasks();
            JOptionPane.showMessageDialog(this, "Tasks saved successfully!");
        }
    }

    private void scheduleReminder(String taskName, LocalDateTime dueTime) {
        Timer timer = new Timer();
        long delay = java.time.Duration.between(LocalDateTime.now(), dueTime).toMillis();
        if (delay > 0) {
            timer.schedule(new TimerTask() {
                public void run() {
                    JOptionPane.showMessageDialog(null, "Reminder: Task '" + taskName + "' is due now!");
                }
            }, delay);
        }
    }
    static class CheckboxListRenderer implements ListCellRenderer<JCheckBox> {
        public Component getListCellRendererComponent(JList<? extends JCheckBox> list, JCheckBox value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            value.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            value.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            value.setEnabled(list.isEnabled());
            value.setFont(list.getFont());
            value.setFocusPainted(false);
            value.setBorderPainted(true);
            return value;
        }
    }

    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (int i = 0; i < taskListModel.size(); i++) {
                JCheckBox task = taskListModel.getElementAt(i);
                writer.write((task.isSelected() ? "[x] " : "[ ] ") + task.getText());
                writer.newLine();
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving tasks: " + ex.getMessage());
        }
    }

    private void loadTasks() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() < 4) continue;
                boolean isSelected = line.startsWith("[x]");
                String taskText = line.substring(4);
                JCheckBox task = new JCheckBox(taskText, isSelected);
                task.setFont(new Font("SansSerif", Font.PLAIN, 16));
                taskListModel.addElement(task);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error loading tasks: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new ToDoListApp();
    }
}

