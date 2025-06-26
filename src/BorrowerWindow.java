import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;





public class BorrowerWindow extends JFrame implements ActionListener {
    private MainProgram mainProgram;

    private JComboBox<String> cboPractitioner, cboItem;
    private JSpinner spinnerQuantity;
    private JTextField txtDate;
    private DatePicker datePickerReturn;

    private JButton btnBorrow, btnReturn, btnCancel, btnBack;
    private JTable tblBorrow;
    private DefaultTableModel model;
    private JPanel infoPanel, formPanel;

    private final String PRACT_FILE = "practitioners.csv";
    private final String INV_FILE = "kendo_inventory.csv";
    private final String BORROW_LOG = "borrow_log.csv";
    private Map<String, Integer> itemQty = new HashMap<>();
    private Database db = new Database();
    private BorrowerDatabase borrowerDb = new BorrowerDatabase();


    public BorrowerWindow() {
        setTitle("Borrower Manager");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(1000, 600);
        setLocationRelativeTo(null);

        initComponents();
        borrowerDb.loadActiveBorrows(model);
        checkOverdueRows();
        loadPractitioners();
        loadItems();
        loadBorrowLog();
        setVisible(true);

    }

    private void initComponents() {
        // Initialize components
        cboPractitioner = new JComboBox<>();
        cboItem = new JComboBox<>();
        spinnerQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        txtDate = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 10);
        txtDate.setEditable(false);
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra("dd/MM/yyyy");
        datePickerReturn = new DatePicker(settings);
        datePickerReturn.setDate(LocalDate.now().plusDays(7));

        //  left panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        formPanel.setPreferredSize(new Dimension(400, 300));
        formPanel.setMaximumSize(new Dimension(400, 300));
        formPanel.setMinimumSize(new Dimension(400, 300));

        // Info panel
        infoPanel = new JPanel(new GridLayout(5, 2, 5, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Borrow Info"));
        infoPanel.setMaximumSize(new Dimension(400, 200));
        infoPanel.setPreferredSize(new Dimension(400, 200));
        infoPanel.setMinimumSize(new Dimension(400, 200));

        infoPanel.add(new JLabel("Practitioner:")); infoPanel.add(cboPractitioner);
        infoPanel.add(new JLabel("Item:")); infoPanel.add(cboItem);
        infoPanel.add(new JLabel("Quantity:")); infoPanel.add(spinnerQuantity);
        infoPanel.add(new JLabel("Date:")); infoPanel.add(txtDate);
        infoPanel.add(new JLabel("Return Date:"));infoPanel.add(datePickerReturn);

        JPanel borrowActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));


        model = new DefaultTableModel(new String[]{"Practitioner", "Item", "Qty", "Date", "Return Date"}, 0);
        tblBorrow = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tblBorrow);

        // Button panel
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnBorrow = new JButton("Borrow");
        btnReturn = new JButton("Return");
        btnCancel = new JButton("Close");
        btnBack = new JButton("Back");


        borrowActionPanel.add(btnBorrow);
        borrowActionPanel.add(btnReturn);
        buttonPanel.add(btnBack, BorderLayout.WEST);
        buttonPanel.add(btnCancel, BorderLayout.EAST);

        formPanel.add(infoPanel);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(borrowActionPanel);
        formPanel.add(Box.createVerticalStrut(20));

        btnBorrow.addActionListener(this);
        btnReturn.addActionListener(this);
        btnCancel.addActionListener(this);
        btnBack.addActionListener(this);

        // Add to frame
        add(formPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        cboItem.addActionListener(e -> updateSpinner());
    }

    public BorrowerWindow(MainProgram mainProgram) {
        this.mainProgram = mainProgram;
        setTitle("Borrower Manager");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setSize(1000, 600);
        setLocationRelativeTo(null);

        initComponents();
        borrowerDb.loadActiveBorrows(model);
        checkOverdueRows();
        loadPractitioners();
        loadItems();
        loadBorrowLog();
        setVisible(true);
    }


    private void markItemAsAvailable(String itemID) {
        try {
            List<String> updatedLines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader("kendo_inventory.csv"));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    String id = parts[0].replace("\"", "").trim();
                    if (id.equals(itemID)) {
                        parts[2] = "\"Available\"";
                    }
                    updatedLines.add(String.join(",", parts));
                } else {
                    updatedLines.add(line);
                }
            }
            reader.close();

            PrintWriter writer = new PrintWriter(new FileWriter("kendo_inventory.csv"));
            for (String updatedLine : updatedLines) {
                writer.println(updatedLine);
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to mark item as available.");
        }
    }



    private void markItemAsBorrowed(String itemID) {
        try {
            List<String> updatedLines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader("kendo_inventory.csv"));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    String id = parts[0].replace("\"", "").trim();
                    if (id.equals(itemID)) {
                        parts[2] = "\"Borrowed\""; // Ensure quotes around new status
                    }
                    updatedLines.add(String.join(",", parts));
                } else {
                    updatedLines.add(line); // Add line unchanged if unexpected format
                }
            }
            reader.close();

            PrintWriter writer = new PrintWriter(new FileWriter("kendo_inventory.csv"));
            for (String updatedLine : updatedLines) {
                writer.println(updatedLine);
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to mark item as borrowed.");
        }
    }


    private void loadBorrowLog() {
        model.setRowCount(0); // Clear table before loading
        try (BufferedReader br = new BufferedReader(new FileReader(BORROW_LOG))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    model.addRow(new Object[]{
                            parts[0].trim(), // Practitioner
                            parts[1].trim(), // Item
                            parts[2].trim(), // Qty
                            parts[3].trim(), // Borrow Date
                            parts[4].trim()  // Return Date
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        checkOverdueRows(); // Refresh row highlights
    }

    private void loadPractitioners() {
        cboPractitioner.removeAllItems();
        try (Scanner sc = new Scanner(new File(PRACT_FILE))) {
            while (sc.hasNextLine()) {
                String[] r = sc.nextLine().split(",");
                String entry = r[0].trim() + " | " + r[1].trim();
                cboPractitioner.addItem(entry);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void checkOverdueRows() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();

        Set<Integer> overdueRows = new HashSet<>();

        for (int i = 0; i < model.getRowCount(); i++) {
            String returnDateStr = model.getValueAt(i, 4).toString();
            try {
                LocalDate returnDate = LocalDate.parse(returnDateStr, formatter);
                if (returnDate.isBefore(today)) {
                    overdueRows.add(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        tblBorrow.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (overdueRows.contains(row)) {
                    c.setBackground(Color.RED);
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        });
    }




    private void loadItems() {
        cboItem.removeAllItems();
        itemQty.clear();
        try (Scanner sc = new Scanner(new File(INV_FILE))) {
            while (sc.hasNextLine()) {
                String[] r = sc.nextLine().split(",");
                String id = r[0].replace("\"", "").trim();
                String name = r[1].replace("\"", "").trim();
                int q = Integer.parseInt(r[4].replace("\"", "").trim());
                cboItem.addItem(id + " | " + name);


                itemQty.put(id + " | " + name, q);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        updateSpinner();
    }

    private void updateSpinner() {
        String id = (String) cboItem.getSelectedItem();
        int max = id != null ? itemQty.getOrDefault(id, 0) : 0;
        int safeMax = Math.max(1, max);

        spinnerQuantity.setModel(new SpinnerNumberModel(1, 1, safeMax, 1));
    }


    private void saveInventory(String itemID, int change) {
        List<String> lines = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(INV_FILE))) {
            while (sc.hasNextLine()) {
                String[] r = sc.nextLine().split(",");
                if (r[0].replace("\"", "").trim().equals(itemID)) {
                    int q = Integer.parseInt(r[4].replace("\"", "").trim()) + change;
                    r[4] = "\"" + q + "\"";
                }
                lines.add(String.join(",", r));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(INV_FILE))) {
            lines.forEach(pw::println);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        loadItems();
    }

    private void appendLog(String prac, String item, int qty, String returnDate) {
        String entry = String.join(",", prac, item, String.valueOf(qty), txtDate.getText(), returnDate);
        try (FileWriter fw = new FileWriter(BORROW_LOG, true)) {
            fw.write(entry + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBorrow) {
            String prac = (String) cboPractitioner.getSelectedItem();
            String item = (String) cboItem.getSelectedItem();
            int qty = (int) spinnerQuantity.getValue();
            String selectedItemID = cboItem.getSelectedItem().toString().split(" \\| ")[0];
            if (qty > itemQty.getOrDefault(item, 0)) {
                JOptionPane.showMessageDialog(this, "Not enough stock");
                return;
            }
            LocalDate returnDate = datePickerReturn.getDate();
            String returnDateStr = returnDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            model.addRow(new Object[]{prac, item, qty, txtDate.getText(), returnDateStr});
            checkOverdueRows();
            saveInventory(item, -qty);
            borrowerDb.saveBorrow(prac, item, qty, txtDate.getText(), returnDateStr);
            markItemAsBorrowed(selectedItemID);
            if (mainProgram != null) {
                mainProgram.reloadTable();
            }

        } else if (e.getSource() == btnReturn) {
            int row = tblBorrow.getSelectedRow();
            if (row >= 0) {
                String prac = model.getValueAt(row, 0).toString();
                String item = model.getValueAt(row, 1).toString();
                int qty = Integer.parseInt(model.getValueAt(row, 2).toString());
                String borrowDate = model.getValueAt(row, 3).toString();
                String returnDate = model.getValueAt(row, 4).toString();
                String itemID = item.split(" \\| ")[0];

                saveInventory(item, qty);
                markItemAsAvailable(itemID);
                borrowerDb.removeBorrow(prac, item, qty, borrowDate, returnDate);
                model.removeRow(row);
            }
    } else if (e.getSource() == btnCancel || e.getSource() == btnBack) {
            dispose();
            new MainProgram();
        }
    }

    public static void main(String[] args) {
        new BorrowerWindow();
    }
}
