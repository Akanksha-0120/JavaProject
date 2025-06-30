import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

class LoginWindow extends JFrame {
    public LoginWindow() {
        setTitle("Library Login");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();

        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);

        JButton loginBtn = new JButton("Login");
        panel.add(new JLabel());
        panel.add(loginBtn);

        loginBtn.addActionListener(e -> {
            if (userField.getText().equals("admin") && new String(passField.getPassword()).equals("admin")) {
                dispose();
                new MainDashboard();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        add(panel);
        setVisible(true);
    }
}

class DataStorage {
    public static List<String[]> readData(String fileName) {
        List<String[]> dataList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                dataList.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }

    public static void writeData(String fileName, List<String[]> dataList) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
            for (String[] entry : dataList) {
                pw.println(String.join(",", entry));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class MainDashboard extends JFrame {
    public MainDashboard() {
        setTitle("Library Dashboard");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JButton addBookBtn = new JButton("Add Book");
        JButton viewBookBtn = new JButton("View Books");
        JButton addMemberBtn = new JButton("Add Member");
        JButton viewMemberBtn = new JButton("View Members");
        JButton issueBookBtn = new JButton("Issue Book");
        JButton returnBookBtn = new JButton("Return Book");
        JButton recordBtn = new JButton("Record");
        JButton logoutBtn = new JButton("Logout");

        panel.add(addBookBtn);
        panel.add(viewBookBtn);
        panel.add(addMemberBtn);
        panel.add(viewMemberBtn);
        panel.add(issueBookBtn);
        panel.add(returnBookBtn);
        panel.add(recordBtn);
        panel.add(logoutBtn);

        addBookBtn.addActionListener(e -> new AddBookPanel());
        viewBookBtn.addActionListener(e -> new ViewBookPanel());
        addMemberBtn.addActionListener(e -> new AddMemberPanel());
        viewMemberBtn.addActionListener(e -> new ViewMemberPanel());
        issueBookBtn.addActionListener(e -> new IssueBookPanel());
        returnBookBtn.addActionListener(e -> new ReturnBookPanel());
        recordBtn.addActionListener(e -> new RecordPanel());
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginWindow();
        });

        add(panel);
        setVisible(true);
    }
}

class ViewMemberPanel extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public ViewMemberPanel() {
        setTitle("View Members");
        setSize(600, 400);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Contact"}, 0);
        table = new JTable(model);
        loadMembers();

        JScrollPane scroll = new JScrollPane(table);
        JButton deleteBtn = new JButton("Delete Selected");

        deleteBtn.addActionListener(e -> deleteSelected());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(deleteBtn);

        add(scroll, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void loadMembers() {
        model.setRowCount(0);
        List<String[]> members = DataStorage.readData("members.txt");
        for (String[] m : members) {
            model.addRow(m);
        }
    }

    private void deleteSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            model.removeRow(selectedRow);
            List<String[]> members = DataStorage.readData("members.txt");
            members.remove(selectedRow);
            DataStorage.writeData("members.txt", members);
        } else {
            JOptionPane.showMessageDialog(this, "No row selected.");
        }
    }
}

class AddBookPanel extends JFrame {
    public AddBookPanel() {
        setTitle("Add Book");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField qtyField = new JTextField();

        panel.add(new JLabel("Book ID:"));
        panel.add(idField);
        panel.add(new JLabel("Book Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Author:"));
        panel.add(authorField);
        panel.add(new JLabel("Quantity:"));
        panel.add(qtyField);

        JButton saveBtn = new JButton("Save");
        panel.add(new JLabel());
        panel.add(saveBtn);

        saveBtn.addActionListener(e -> {
            String[] book = {
                idField.getText(), nameField.getText(), authorField.getText(), qtyField.getText()
            };
            List<String[]> books = DataStorage.readData("books.txt");
            books.add(book);
            DataStorage.writeData("books.txt", books);
            JOptionPane.showMessageDialog(this, "Book added!");
            dispose();
        });

        add(panel);
        setVisible(true);
    }
}
class AddMemberPanel extends JFrame {
    public AddMemberPanel() {
        setTitle("Add Member");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();

        panel.add(new JLabel("Member ID:"));
        panel.add(idField);
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Contact:"));
        panel.add(contactField);

        JButton saveBtn = new JButton("Save");
        panel.add(new JLabel());
        panel.add(saveBtn);

        saveBtn.addActionListener(e -> {
            String[] member = { idField.getText(), nameField.getText(), contactField.getText() };
            List<String[]> members = DataStorage.readData("members.txt");
            members.add(member);
            DataStorage.writeData("members.txt", members);
            JOptionPane.showMessageDialog(this, "Member added!");
            dispose();
        });

        add(panel);
        setVisible(true);
    }
}
class IssueBookPanel extends JFrame {
    public IssueBookPanel() {
        setTitle("Issue Book");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField memberIdField = new JTextField();
        JTextField bookIdField = new JTextField();
        JTextField issueDateField = new JTextField(new java.text.SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        JTextField returnDateField = new JTextField();

        panel.add(new JLabel("Member ID:"));
        panel.add(memberIdField);
        panel.add(new JLabel("Book ID:"));
        panel.add(bookIdField);
        panel.add(new JLabel("Issue Date:"));
        panel.add(issueDateField);
        panel.add(new JLabel("Return Date:"));
        panel.add(returnDateField);

        JButton issueBtn = new JButton("Issue");
        panel.add(new JLabel());
        panel.add(issueBtn);

        issueBtn.addActionListener(e -> {
            String mid = memberIdField.getText();
            String bid = bookIdField.getText();
            String issue = issueDateField.getText();
            String ret = returnDateField.getText();

            if (mid.isEmpty() || bid.isEmpty() || issue.isEmpty() || ret.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields required");
                return;
            }

            List<String[]> data = DataStorage.readData("issues.txt");
            data.add(new String[]{mid, bid, issue, ret});
            DataStorage.writeData("issues.txt", data);
            JOptionPane.showMessageDialog(this, "Book Issued!");
            dispose();
        });

        add(panel);
        setVisible(true);
    }
}


class ReturnBookPanel extends JFrame {
    private JTextField memberIdField;
    private JTextField bookIdField;
    private JLabel returnDateLabel;
    private JLabel elapsedLabel;
    private JLabel fineLabel;

    public ReturnBookPanel() {
        setTitle("Return Book");
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        memberIdField = new JTextField();
        bookIdField = new JTextField();
        returnDateLabel = new JLabel("-");
        elapsedLabel = new JLabel("-");
        fineLabel = new JLabel("-");

        panel.add(new JLabel("Member ID:"));
        panel.add(memberIdField);
        panel.add(new JLabel("Book ID:"));
        panel.add(bookIdField);
        panel.add(new JLabel("Return Date:"));
        panel.add(returnDateLabel);
        panel.add(new JLabel("Elapsed Days:"));
        panel.add(elapsedLabel);
        panel.add(new JLabel("Fine (₹50/day):"));
        panel.add(fineLabel);

        JButton returnBtn = new JButton("Return Book");
        panel.add(new JLabel());
        panel.add(returnBtn);

        KeyAdapter keyHandler = new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                checkAndDisplayDetails();
            }
        };

        memberIdField.addKeyListener(keyHandler);
        bookIdField.addKeyListener(keyHandler);

        returnBtn.addActionListener(e -> processReturn());

        add(panel);
        setVisible(true);
    }

    private void checkAndDisplayDetails() {
        String mid = memberIdField.getText().trim();
        String bid = bookIdField.getText().trim();

        if (mid.isEmpty() || bid.isEmpty()) return;

        List<String[]> data = DataStorage.readData("issues.txt");

        for (String[] row : data) {
            if (row.length >= 4 && row[0].equals(mid) && row[1].equals(bid)) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date returnDate = sdf.parse(row[3]);
                    returnDateLabel.setText(row[3]);

                    Date today = new Date();
                    long diff = today.getTime() - returnDate.getTime();
                    long daysLate = diff / (1000 * 60 * 60 * 24);
                    int fine = (daysLate > 0) ? (int) daysLate * 50 : 0;

                    elapsedLabel.setText(String.valueOf(Math.max(daysLate, 0)));
                    fineLabel.setText("₹" + fine);
                } catch (Exception ex) {
                    returnDateLabel.setText("Error");
                    elapsedLabel.setText("-");
                    fineLabel.setText("-");
                }
                return;
            }
        }

        // If not found
        returnDateLabel.setText("-");
        elapsedLabel.setText("-");
        fineLabel.setText("-");
    }

    private void processReturn() {
        String mid = memberIdField.getText().trim();
        String bid = bookIdField.getText().trim();
        List<String[]> data = DataStorage.readData("issues.txt");
        boolean found = false;

        Iterator<String[]> iterator = data.iterator();
        while (iterator.hasNext()) {
            String[] row = iterator.next();
            if (row.length >= 4 && row[0].equals(mid) && row[1].equals(bid)) {
                iterator.remove();
                DataStorage.writeData("issues.txt", data);

                try (FileWriter writer = new FileWriter("returns.txt", true)) {
                    String todayStr = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                    String fineText = fineLabel.getText().replace("₹", "");
                    writer.write(mid + "," + bid + "," + todayStr + "," + fineText + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                JOptionPane.showMessageDialog(this, "Book returned successfully!");
                    dispose();
                    new MainDashboard();
                    break;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Record not found");
        }
    }
}

class RecordPanel extends JFrame {
    public RecordPanel() {
        setTitle("Book Issue and Return Records");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // Book Issue Records
        String[] issueCols = {"Member ID", "Book ID", "Issue Date", "Return Date"};
        java.util.List<String[]> issueData = DataStorage.readData("issues.txt");
        String[][] issueRows = issueData.stream()
            .filter(row -> row.length >= 4)
            .toArray(String[][]::new);
        JTable issueTable = new JTable(issueRows, issueCols);
        JScrollPane issueScroll = new JScrollPane(issueTable);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JLabel("Book Issue Records", SwingConstants.CENTER), BorderLayout.NORTH);
        leftPanel.add(issueScroll, BorderLayout.CENTER);

        // Book Return Records
        String[] returnCols = {"Returned Member ID", "Book ID", "Returned On", "Fine"};
        java.util.List<String[]> returnData = DataStorage.readData("returns.txt");
        String[][] returnRows = returnData.stream()
            .filter(row -> row.length >= 4)
            .toArray(String[][]::new);
        JTable returnTable = new JTable(returnRows, returnCols);
        JScrollPane returnScroll = new JScrollPane(returnTable);
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("Book Return Records", SwingConstants.CENTER), BorderLayout.NORTH);
        rightPanel.add(returnScroll, BorderLayout.CENTER);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel);
        setVisible(true);
    }
}



class ViewBookPanel extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public ViewBookPanel() {
        setTitle("Manage Books");
        setSize(700, 400);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Qty"}, 0);
        table = new JTable(model);
        loadBooks();

        JScrollPane scrollPane = new JScrollPane(table);

        JButton editBtn = new JButton("✎ Edit");
        JButton deleteBtn = new JButton("🗑 Delete");
        JButton printBtn = new JButton("🖨 Print");

        JPanel btnPanel = new JPanel();
        btnPanel.add(editBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(printBtn);

        editBtn.addActionListener(e -> editSelected());
        deleteBtn.addActionListener(e -> deleteSelected());
        printBtn.addActionListener(e -> printTable());

        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void loadBooks() {
        model.setRowCount(0);
        List<String[]> books = DataStorage.readData("books.txt");
        for (String[] book : books) {
            model.addRow(book);
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String[] updated = new String[4];
            for (int i = 0; i < 4; i++) {
                updated[i] = JOptionPane.showInputDialog(this, "Edit " + model.getColumnName(i), model.getValueAt(row, i));
            }
            for (int i = 0; i < 4; i++) model.setValueAt(updated[i], row, i);

            List<String[]> books = DataStorage.readData("books.txt");
            books.set(row, updated);
            DataStorage.writeData("books.txt", books);
        } else {
            JOptionPane.showMessageDialog(this, "Select a row to edit");
        }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            model.removeRow(row);
            List<String[]> books = DataStorage.readData("books.txt");
            books.remove(row);
            DataStorage.writeData("books.txt", books);
        } else {
            JOptionPane.showMessageDialog(this, "Select a row to delete");
        }
    }

    private void printTable() {
        try {
            boolean printed = table.print();
            if (printed) {
                JOptionPane.showMessageDialog(this, "Printed successfully");
            }
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }
   public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ViewBookPanel());
}
}