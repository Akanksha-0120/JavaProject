import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.ArrayList;

public class UnitConverterApp extends JFrame implements ActionListener {
    JComboBox<String> categoryBox, fromUnitBox, toUnitBox;
    JTextField inputField, resultField;
    JButton convertBtn, copyBtn, deleteHistoryBtn;
    JLabel lastConversionLabel;
    DefaultListModel<String> historyListModel;
    JList<String> historyList;
    final String LAST_CONVERSION_FILE = "last_conversion.txt";
    final String HISTORY_FILE = "conversion_history.txt";

    public UnitConverterApp() {
        setTitle("Unit Converter App");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(6, 2, 10, 10));

        String[] categories = {"Length", "Weight", "Temperature", "Area", "Volume", "Speed", "Pressure", "Power"};
        categoryBox = new JComboBox<>(categories);
        categoryBox.addActionListener(e -> updateUnits());

        fromUnitBox = new JComboBox<>();
        toUnitBox = new JComboBox<>();
        updateUnits();

        inputField = new JTextField();
        resultField = new JTextField();
        resultField.setEditable(false);

        convertBtn = new JButton("Convert");
        convertBtn.addActionListener(this);

        copyBtn = new JButton("Copy Result");
        copyBtn.addActionListener(e -> {
            StringSelection selection = new StringSelection(resultField.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);
            JOptionPane.showMessageDialog(this, "Result copied to clipboard.");
        });
        topPanel.add(new JLabel("Category:")); topPanel.add(categoryBox);
        topPanel.add(new JLabel("From Unit:")); topPanel.add(fromUnitBox);
        topPanel.add(new JLabel("To Unit:")); topPanel.add(toUnitBox);
        topPanel.add(new JLabel("Value:")); topPanel.add(inputField);
        topPanel.add(new JLabel("Result:")); topPanel.add(resultField);
        topPanel.add(convertBtn); topPanel.add(copyBtn);

        add(topPanel, BorderLayout.NORTH);

        lastConversionLabel = new JLabel("Last: ");
        loadLastConversion();

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("Last Conversion:"), BorderLayout.NORTH);
        centerPanel.add(lastConversionLabel, BorderLayout.CENTER);

        historyListModel = new DefaultListModel<>();
        historyList = new JList<>(historyListModel);
        loadHistory();

        JScrollPane historyScroll = new JScrollPane(historyList);
        centerPanel.add(new JLabel("History Log:"), BorderLayout.SOUTH);
        centerPanel.add(historyScroll, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        deleteHistoryBtn = new JButton("Delete Selected History");
        deleteHistoryBtn.addActionListener(e -> {
            int index = historyList.getSelectedIndex();
            if (index != -1) {
                historyListModel.remove(index);
                saveHistory();
                JOptionPane.showMessageDialog(this, "Selected history entry deleted.");
            }
        });

        add(deleteHistoryBtn, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void updateUnits() {
        fromUnitBox.removeAllItems();
        toUnitBox.removeAllItems();
        String category = (String) categoryBox.getSelectedItem();

        String[] units;
        switch (category) {
            case "Length" -> units = new String[]{"Meter", "Kilometer", "Mile", "Inch"};
            case "Weight" -> units = new String[]{"Kilogram", "Gram", "Pound"};
            case "Temperature" -> units = new String[]{"Celsius", "Fahrenheit", "Kelvin"};
            case "Area" -> units = new String[]{"Square Meter", "Square Kilometer", "Hectare", "Acre"};
            case "Volume" -> units = new String[]{"Liter", "Milliliter", "Cubic Meter", "Gallon"};
            case "Speed" -> units = new String[]{"m/s", "km/h", "mph"};
            case "Pressure" -> units = new String[]{"Pascal", "Bar", "PSI"};
            case "Power" -> units = new String[]{"Watt", "Kilowatt", "Horsepower"};
            default -> units = new String[]{};
        }
        for (String u : units) {
            fromUnitBox.addItem(u);
            toUnitBox.addItem(u);
        }
    }

    public void actionPerformed(ActionEvent e) {
        try {
            double inputValue = Double.parseDouble(inputField.getText());
            String fromUnit = (String) fromUnitBox.getSelectedItem();
            String toUnit = (String) toUnitBox.getSelectedItem();
            String category = (String) categoryBox.getSelectedItem();

            double result = switch (category) {
                case "Length" -> convertLength(inputValue, fromUnit, toUnit);
                case "Weight" -> convertWeight(inputValue, fromUnit, toUnit);
                case "Temperature" -> convertTemperature(inputValue, fromUnit, toUnit);
                case "Area" -> convertArea(inputValue, fromUnit, toUnit);
                case "Volume" -> convertVolume(inputValue, fromUnit, toUnit);
                case "Speed" -> convertSpeed(inputValue, fromUnit, toUnit);
                case "Pressure" -> convertPressure(inputValue, fromUnit, toUnit);
                case "Power" -> convertPower(inputValue, fromUnit, toUnit);
                default -> 0;
            };

            String resultText = String.format("%.2f", result);
            resultField.setText(resultText);

            String conversionSummary = inputValue + " " + fromUnit + " = " + resultText + " " + toUnit;
            lastConversionLabel.setText(conversionSummary);
            saveLastConversion(conversionSummary);
            historyListModel.addElement(conversionSummary);
            saveHistory();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number.");
        }
    }

    private double convertLength(double value, String from, String to) {
        double meters = switch (from) {
            case "Kilometer" -> value * 1000;
            case "Mile" -> value * 1609.34;
            case "Inch" -> value * 0.0254;
            default -> value;
        };
        return switch (to) {
            case "Kilometer" -> meters / 1000;
            case "Mile" -> meters / 1609.34;
            case "Inch" -> meters / 0.0254;
            default -> meters;
        };
    }

    private double convertWeight(double value, String from, String to) {
        double kg = switch (from) {
            case "Gram" -> value / 1000;
            case "Pound" -> value / 2.20462;
            default -> value;
        };
        return switch (to) {
            case "Gram" -> kg * 1000;
            case "Pound" -> kg * 2.20462;
            default -> kg;
        };
    }

    private double convertTemperature(double value, String from, String to) {
        if (from.equals(to)) return value;
        double celsius = switch (from) {
            case "Fahrenheit" -> (value - 32) * 5 / 9;
            case "Kelvin" -> value - 273.15;
            default -> value;
        };
        return switch (to) {
            case "Fahrenheit" -> celsius * 9 / 5 + 32;
            case "Kelvin" -> celsius + 273.15;
            default -> celsius;
        };
    }

    private double convertArea(double value, String from, String to) {
        double squareMeters = switch (from) {
            case "Square Kilometer" -> value * 1_000_000;
            case "Hectare" -> value * 10_000;
            case "Acre" -> value * 4046.86;
            default -> value;
        };
        return switch (to) {
            case "Square Kilometer" -> squareMeters / 1_000_000;
            case "Hectare" -> squareMeters / 10_000;
            case "Acre" -> squareMeters / 4046.86;
            default -> squareMeters;
        };
    }

    private double convertVolume(double value, String from, String to) {
        double liters = switch (from) {
            case "Milliliter" -> value / 1000;
            case "Cubic Meter" -> value * 1000;
            case "Gallon" -> value * 3.78541;
            default -> value;
        };
        return switch (to) {
            case "Milliliter" -> liters * 1000;
            case "Cubic Meter" -> liters / 1000;
            case "Gallon" -> liters / 3.78541;
            default -> liters;
        };
    }

    private double convertSpeed(double value, String from, String to) {
        double mps = switch (from) {
            case "km/h" -> value / 3.6;
            case "mph" -> value * 0.44704;
            default -> value;
        };
        return switch (to) {
            case "km/h" -> mps * 3.6;
            case "mph" -> mps / 0.44704;
            default -> mps;
        };
    }

    private double convertPressure(double value, String from, String to) {
        double pascal = switch (from) {
            case "Bar" -> value * 100000;
            case "PSI" -> value * 6894.76;
            default -> value;
        };
        return switch (to) {
            case "Bar" -> pascal / 100000;
            case "PSI" -> pascal / 6894.76;
            default -> pascal;
        };
    }

    private double convertPower(double value, String from, String to) {
        double watts = switch (from) {
            case "Kilowatt" -> value * 1000;
            case "Horsepower" -> value * 745.7;
            default -> value;
        };
        return switch (to) {
            case "Kilowatt" -> watts / 1000;
            case "Horsepower" -> watts / 745.7;
            default -> watts;
        };
    }

    private void saveLastConversion(String text) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LAST_CONVERSION_FILE))) {
            writer.write(text);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadLastConversion() {
        File file = new File(LAST_CONVERSION_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String last = reader.readLine();
            if (last != null) lastConversionLabel.setText(last);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void saveHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            for (int i = 0; i < historyListModel.size(); i++) {
                writer.write(historyListModel.getElementAt(i));
                writer.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void loadHistory() {
        File file = new File(HISTORY_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                historyListModel.addElement(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UnitConverterApp();
    }
}
