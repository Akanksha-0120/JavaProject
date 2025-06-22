import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleCalculator extends JFrame implements ActionListener {
    JTextField display;
    String operator = "", input = "";
    double num1 = 0;

    public SimpleCalculator() {
        setTitle("Calculator");
        setSize(300, 400);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setText("0");
        display.setHorizontalAlignment(JLabel.RIGHT);
        add(display, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(6, 4, 5, 5));
        String[] buttons = {"7","8","9","/",
                            "4","5","6","*",
                            "1","2","3","-",
                            "0","00","=","+",
                            "%", "AC", "←", "."}; // Using left arrow for backspace

        for (String b : buttons) {
            JButton btn = new JButton(b);
            btn.setFont(new Font("Arial", Font.BOLD, 18));
            btn.addActionListener(this);
            panel.add(btn);
        }

        add(panel);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        if ("0123456789.00".contains(cmd) && !cmd.equals("←")) {
            input += cmd;
            display.setText(input);
        } else if ("+-*/%".contains(cmd)) {
            operator = cmd;
            num1 = Double.parseDouble(input);
            input = "";
        } else if (cmd.equals("=")) {
            double num2 = Double.parseDouble(input);
            double result = switch (operator) {
                case "+" -> num1 + num2;
                case "-" -> num1 - num2;
                case "*" -> num1 * num2;
                case "/" -> num2 != 0 ? num1 / num2 : 0;
                case "%" -> num1 % num2;
                default -> 0;
            };
            display.setText(String.valueOf(result));
            input = String.valueOf(result);
        } else if (cmd.equals("AC")) {
            input = "";
            operator = "";
            num1 = 0;
            display.setText("0");
        } else if (cmd.equals("←")) {
            if (input.length() > 0) {
                input = input.substring(0, input.length() - 1);
                display.setText(input.length() == 0 ? "0" : input);
            }
        }
    }

    public static void main(String[] args) {
        new SimpleCalculator();
    }
}
