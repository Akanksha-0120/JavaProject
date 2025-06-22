/**import java.util.ArrayList;
import java.util.Scanner;
//You have zero balance saving account
 class ATM{
    private double balance;
    private ArrayList<String> transactionHistory;

    public ATM() {
        this.balance = 0.0;
        this.transactionHistory = new ArrayList<>();
    }

    public void checkBalance() {
        System.out.printf("Your current balance is: %.2f\n", balance);
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Invalid deposit amount.");
            return;
        }
        balance += amount;
        transactionHistory.add("Deposited: " + amount);
        System.out.println("" + amount + " deposited successfully.");
    }

    public void withdraw(double amount) {
        if (amount > balance) {
            System.out.println("Insufficient balance.");
        } else if (amount <= 0) {
            System.out.println("Invalid withdrawal amount.");
        } else {
            balance -= amount;
            transactionHistory.add("Withdrew: " + amount);
            System.out.println("" + amount + " withdrawn successfully.");
        }
    }

    public void showTransactionHistory() {
        if (!transactionHistory.isEmpty()) {
            System.out.println("Transaction History:");
            for (String record : transactionHistory) {
                System.out.println(" - " + record);
            }
        } else {
            System.out.println("No transactions yet.");
        }
   } 
}

public class FunctionOfATM{
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ATM atm = new ATM();

        System.out.println("Welcome to the ATM Machine!");
        boolean function = true;

        while (function) {
            System.out.println("\nChoose an option:");
            System.out.println("1  Deposit");
            System.out.println("2  Check Balance");
            System.out.println("3  Withdraw");
            System.out.println("4  Transaction History");
            System.out.println("5  Exit");

            System.out.print("Enter your option (1-5): ");
            int option = sc.nextInt();

            switch (option) {
                case 1:
                    System.out.print("Enter deposit amount: ");
                    double depositAmt = sc.nextDouble();
                    atm.deposit(depositAmt);
                    break;
                case 2:
                    atm.checkBalance();
                    break;
                case 3:
                    System.out.print("Enter withdrawal amount: ");
                    double withdrawAmt = sc.nextDouble();
                    atm.withdraw(withdrawAmt);
                    break;
                case 4:
                    atm.showTransactionHistory();
                    break;
                case 5:
                    System.out.println("Thank you for your valuable time.Have a nice Day!");
                    function = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        sc.close();
    }
}**/

/**import java.util.Scanner;

public class FunctionOfATM {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Step 1: Let user set their own PIN
        printBox("Set your 4-digit ATM PIN:");
        int userPIN = sc.nextInt();

        // Step 2: Authenticate
        int attempts = 0;
        final int maxAttempts = 3;

        while (attempts < maxAttempts) {
            printBox("Enter your 4-digit PIN to login:");
            int enteredPIN = sc.nextInt();

            if (enteredPIN == userPIN) {
                printBox("PIN verified. Access granted.");
                atmMenu(sc);
                break;
            } else {
                attempts++;
                printBox("Wrong PIN. Attempts left: " + (maxAttempts - attempts));
            }

            if (attempts == maxAttempts) {
                printBox("Access Denied. Too many incorrect attempts.");
                return;
            }
        }

        sc.close();
    }

    // ATM Functional Menu
    public static void atmMenu(Scanner sc) {
        double balance = 1000.0;
        int choice;

        do {
            printBox(
                "====== ATM MENU ======\n" +
                "1. Withdraw\n" +
                "2. Deposit\n" +
                "3. Check Balance\n" +
                "4. Exit\n" +
                "Choose an option:"
            );
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    printBox("Enter amount to withdraw: ₹");
                    double withdraw = sc.nextDouble();
                    if (withdraw > balance) {
                        printBox("Insufficient balance.");
                    } else {
                        balance -= withdraw;
                        printBox("" + withdraw + " withdrawn.");
                    }
                    break;

                case 2:
                    printBox("Enter amount to deposit: ₹");
                    double deposit = sc.nextDouble();
                    balance += deposit;
                    printBox("" + deposit + " deposited.");
                    break;

                case 3:
                    printBox(String.format("Current Balance: ₹%.2f", balance));
                    break;

                case 4:
                    printBox("Thank you for using the ATM.");
                    break;

                default:
                    printBox("Invalid option. Try again.");
            }

        } while (choice != 4);
    }

    // Utility: Box-style output
    public static void printBox(String message) {
        String[] lines = message.split("\n");
        int maxLength = 0;
        for (String line : lines) {
            if (line.length() > maxLength) maxLength = line.length();
        }

        String border = "+" + "-".repeat(maxLength + 2) + "+";
        System.out.println(border);
        for (String line : lines) {
            System.out.printf("| %-"+ maxLength +"s |\n", line);
        }
        System.out.println(border);
    }
}**/
/**import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ATMPinLogin extends JFrame implements ActionListener {
    private JPasswordField pinField;
    private JButton loginButton;

    public ATMPinLogin() {
        setTitle("ATM Login");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Welcome to ATM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(titleLabel, gbc);

        JLabel pinLabel = new JLabel("Enter your PIN:");
        pinLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(pinLabel, gbc);

        pinField = new JPasswordField(10);
        gbc.gridx = 1;
        add(pinField, gbc);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String pin = new String(pinField.getPassword());

        if (pin.matches("\\d{4}")) {
            dispose();  // close login frame
            new ATMMainMenu();  // open main ATM menu
        } else {
            JOptionPane.showMessageDialog(this, "Invalid PIN! Please enter a 4-digit number.");
        }
    }

    public static void main(String[] args) {
        new ATMPinLogin();
    }
}**/

// User.java - holds user-specific data
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

 class User {
    private String pin;
    private double balance;
    private ArrayList<String> history;

    public User(String pin) {
        this.pin = pin;
        this.balance = 0.0;
        this.history = new ArrayList<>();
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String newPin) {
        this.pin = newPin;
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        balance += amount;
        addHistory("Deposited ₹" + amount);
    }

    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            addHistory("Withdrawn ₹" + amount);
            return true;
        }
        return false;
    }

    public void addHistory(String action) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        history.add(action + " on " + timestamp);
    }

    public ArrayList<String> getHistory() {
        return history;
    }
}
// ATMPinLogin.java - Login Screen

 class FunctionOfATM extends JFrame implements ActionListener {
    private JPasswordField pinField;
    private JButton loginButton;
    private static HashMap<String, User> users = new HashMap<>();

    public FunctionOfATM() {
        setTitle("ATM Login");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Welcome to ATM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(titleLabel, gbc);

        JLabel pinLabel = new JLabel("Enter your PIN:");
        pinLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(pinLabel, gbc);

        pinField = new JPasswordField(10);
        gbc.gridx = 1;
        add(pinField, gbc);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginButton, gbc);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String pin = new String(pinField.getPassword());

        if (!users.containsKey(pin)) {
            users.put(pin, new User(pin));
            JOptionPane.showMessageDialog(this, "Note: This is a zero-balance account. Please deposit at least ₹1000 to start transactions.");
        }
        User currentUser = users.get(pin);
        dispose();
        new ATMMainMenu(currentUser);
    }

    public static void main(String[] args) {
        new FunctionOfATM();
    }
}

class ATMMainMenu extends JFrame implements ActionListener {
    private User currentUser;
    private JButton withdrawBtn, depositBtn, checkBalanceBtn, historyBtn, changePinBtn, exitBtn;

    public ATMMainMenu(User user) {
        this.currentUser = user;

        setTitle("ATM Main Menu");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 1, 10, 10));

        JLabel title = new JLabel("Choose a Transaction", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title);

        withdrawBtn = new JButton("Withdraw");
        depositBtn = new JButton("Deposit");
        checkBalanceBtn = new JButton("Check Balance");
        historyBtn = new JButton("Transaction History");
        changePinBtn = new JButton("Change PIN");
        exitBtn = new JButton("Exit");

        withdrawBtn.addActionListener(this);
        depositBtn.addActionListener(this);
        checkBalanceBtn.addActionListener(this);
        historyBtn.addActionListener(this);
        changePinBtn.addActionListener(this);
        exitBtn.addActionListener(this);

        add(withdrawBtn);
        add(depositBtn);
        add(checkBalanceBtn);
        add(historyBtn);
        add(changePinBtn);
        add(exitBtn);

        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == withdrawBtn) {
            if (currentUser.getBalance() < 1000) {
                JOptionPane.showMessageDialog(this, "Your account is a zero-balance account. Please deposit at least ₹1000 to enable transactions.");
                return;
            }
            String amountStr = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
            try {
                double amount = Double.parseDouble(amountStr);
                if (currentUser.withdraw(amount)) {
                    JOptionPane.showMessageDialog(this, "₹" + amount + " withdrawn successfully.");
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient Balance.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        } else if (e.getSource() == depositBtn) {
            String amountStr = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
            try {
                double amount = Double.parseDouble(amountStr);
                currentUser.deposit(amount);
                JOptionPane.showMessageDialog(this, "₹" + amount + " deposited successfully.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        } else if (e.getSource() == checkBalanceBtn) {
            if (currentUser.getBalance() < 1000) {
                JOptionPane.showMessageDialog(this, "Your account is a zero-balance account. Please deposit at least ₹1000 to check balance.");
                return;
            }
            JOptionPane.showMessageDialog(this, "Your current balance is ₹" + currentUser.getBalance());
        } else if (e.getSource() == historyBtn) {
            java.util.List<String> history = currentUser.getHistory();
            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No transactions yet.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (String record : history) {
                    sb.append(record).append("\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString(), "Transaction History", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (e.getSource() == changePinBtn) {
            String currentPin = JOptionPane.showInputDialog(this, "Enter current PIN:");
            if (!currentUser.getPin().equals(currentPin)) {
                JOptionPane.showMessageDialog(this, "Incorrect current PIN.");
                return;
            }
            String newPin = JOptionPane.showInputDialog(this, "Enter new 4-digit PIN:");
            if (newPin != null && newPin.matches("\\d{4}")) {
                currentUser.setPin(newPin);
                JOptionPane.showMessageDialog(this, "PIN changed successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid new PIN. It must be 4 digits.");
            }
        } else if (e.getSource() == exitBtn) {
            JOptionPane.showMessageDialog(this, "Thank you for using the ATM.");
            System.exit(0);
        }
    }
}