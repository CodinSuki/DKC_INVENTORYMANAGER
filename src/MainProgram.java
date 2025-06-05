import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;

public class MainProgram extends MainFrame implements ActionListener, MouseListener, KeyListener, WindowListener {

    private JLabel lblID, lblType, lblStatus, lblDate, lblQuantity, lblCondition, lblLocation;
    private JTextField txtID, txtDate, txtLocation;
    private JComboBox<String> cboType, cboStatus, cboCondition;
    private JSpinner spinnerQuantity;
    private JButton btnAdd, btnClear, btnUpdate, btnDelete, btnClose;
    private JPanel panelButton, panelTable, panelKendoInfo, panelDynamicFields;

    private Database db = new Database("kendo_inventory.txt");

    private JTable tblKendo;
    private DefaultTableModel model_Kendo;
    private Vector columns, rowData;

    private java.util.List<JTextField> additionalFields = new ArrayList<>();

    public MainProgram() {
        initializedComponents();
        KendoInfo();
        panelKendoButton();

        panelTable = panelKendoTable();
        txtID.setText(getRowCount());

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.add(panelKendoInfo);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(panelDynamicFields);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(leftPanel, BorderLayout.WEST);
        contentPanel.add(panelTable, BorderLayout.CENTER);
        contentPanel.add(panelButton, BorderLayout.SOUTH);

        btnAdd.addActionListener(this);
        btnClear.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnDelete.addActionListener(this);
        btnClose.addActionListener(this);

        tblKendo.addMouseListener(this);
        addWindowListener(this);

        db.displayRecords(model_Kendo);
        resetComponents();

        add(contentPanel);
        setMyFrame("DKC Inventory Manager", 1000, 600, true, DISPOSE_ON_CLOSE, true);
        setLocationRelativeTo(null);
    }

    public void initializedComponents() {
        lblID = new JLabel("ID:");
        lblType = new JLabel("Type:");
        lblStatus = new JLabel("Status:");
        lblDate = new JLabel("Date Acquired:");
        lblQuantity = new JLabel("Quantity:");
        lblCondition = new JLabel("Condition:");
        lblLocation = new JLabel("Storage Location:");

        txtID = new JTextField(20); txtID.setEditable(false);
        txtDate = new JTextField(10); txtLocation = new JTextField(15);

        cboType = new JComboBox<>();
        cboStatus = new JComboBox<>();
        cboCondition = new JComboBox<>(new String[]{"New", "Good", "Fair", "Damaged"});

        spinnerQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        cboStatus.addItem("Available");
        cboStatus.addItem("Borrowed");
        cboStatus.addItem("In Repair");
        cboStatus.addItem("Unavailable");

        cboType.addItem("Select Type");
        cboType.addItem("Shinai");
        cboType.addItem("Men");
        cboType.addItem("Kote");
        cboType.addItem("Dō");
        cboType.addItem("Tare");

        cboType.addActionListener(e -> {
            String selectedType = (String) cboType.getSelectedItem();
            updateTypeSpecificFields(selectedType);
        });
    }

    public void KendoInfo() {
        panelKendoInfo = new JPanel(new GridLayout(7, 2, 5, 5));
        panelKendoInfo.setBorder(BorderFactory.createTitledBorder("Kendo Equipment Registration"));
        panelKendoInfo.setMaximumSize(new Dimension(400, 300));

        panelKendoInfo.add(lblID); panelKendoInfo.add(txtID);
        panelKendoInfo.add(lblType); panelKendoInfo.add(cboType);
        panelKendoInfo.add(lblStatus); panelKendoInfo.add(cboStatus);
        panelKendoInfo.add(lblDate); panelKendoInfo.add(txtDate);
        panelKendoInfo.add(lblQuantity); panelKendoInfo.add(spinnerQuantity);
        panelKendoInfo.add(lblCondition); panelKendoInfo.add(cboCondition);
        panelKendoInfo.add(lblLocation); panelKendoInfo.add(txtLocation);

        panelDynamicFields = new JPanel(new GridLayout(4, 2, 5, 5));
        panelDynamicFields.setBorder(BorderFactory.createTitledBorder("Additional Info"));
        panelDynamicFields.setMaximumSize(new Dimension(400, 180));
    }

    public void panelKendoButton() {
        panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelButton.setBorder(BorderFactory.createTitledBorder("Actions"));

        btnAdd = new JButton("Add");
        btnClear = new JButton("Clear");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClose = new JButton("Close");

        panelButton.add(btnAdd); panelButton.add(btnClear);
        panelButton.add(btnUpdate); panelButton.add(btnDelete);
        panelButton.add(btnClose);
    }

    public void resetComponents() {
        txtID.setText(getRowCount());
        btnAdd.setEnabled(true);
        btnClear.setEnabled(true);
        btnClose.setEnabled(true);
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);

        txtLocation.setText("");
        txtDate.setText("DD/MM/YYYY");
        cboType.setSelectedIndex(0);
        cboStatus.setSelectedIndex(0);
        cboCondition.setSelectedIndex(0);
        spinnerQuantity.setValue(1);

        updateTypeSpecificFields("Select Type");
    }

    public void tableClick() {
        btnAdd.setEnabled(false);
        btnUpdate.setEnabled(true);
        btnDelete.setEnabled(true);
    }

    public void updateTypeSpecificFields(String selectedType) {
        panelDynamicFields.removeAll();
        additionalFields.clear();

        switch (selectedType) {
            case "Shinai":
                addDynamicField("Length:");
                addDynamicField("Weight:");
                addDynamicField("Material:");
                break;
            case "Men":
                addDynamicField("Size:");
                addDynamicField("Padding Type:");
                break;
            case "Kote":
                addDynamicField("Glove Size:");
                break;
            case "Dō":
                addDynamicField("Chest Size:");
                break;
            case "Tare":
                addDynamicField("Waist Size:");
                break;
            default:
                break;
        }

        panelDynamicFields.revalidate();
        panelDynamicFields.repaint();
    }

    private void addDynamicField(String label) {
        panelDynamicFields.add(new JLabel(label));
        JTextField field = new JTextField(10);
        panelDynamicFields.add(field);
        additionalFields.add(field);
    }

    public JPanel panelKendoTable() {
        panelTable = new JPanel(new BorderLayout());
        model_Kendo = new DefaultTableModel();

        String[] cols = {"ID", "Type", "Status", "Date Acquired", "Quantity", "Condition", "Storage Location", "Additional Info"};
        columns = new Vector<>(Arrays.asList(cols));
        model_Kendo.setColumnIdentifiers(columns);

        tblKendo = new JTable(model_Kendo);
        tblKendo.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollPane = new JScrollPane(tblKendo);

        panelTable.add(scrollPane, BorderLayout.CENTER);
        panelTable.setBorder(BorderFactory.createTitledBorder("Kendo Inventory Table"));

        return panelTable;
    }

    public String getRowCount() {
        return "10" + model_Kendo.getRowCount();
    }

    public void getData() {
        rowData = new Vector<>();
        rowData.add(txtID.getText());
        rowData.add(cboType.getSelectedItem());
        rowData.add(cboStatus.getSelectedItem());
        rowData.add(txtDate.getText());
        rowData.add(spinnerQuantity.getValue().toString());
        rowData.add(cboCondition.getSelectedItem());
        rowData.add(txtLocation.getText());

        StringBuilder additionalInfo = new StringBuilder();

        String selectedType = (String) cboType.getSelectedItem();

        String[] labels;
        switch (selectedType) {
            case "Shinai": labels = new String[]{"Length", "Weight", "Material"}; break;
            case "Men": labels = new String[]{"Size", "Padding Type"}; break;
            case "Kote": labels = new String[]{"Glove Size"}; break;
            case "Dō": labels = new String[]{"Chest Size"}; break;
            case "Tare": labels = new String[]{"Waist Size"}; break;
            default: labels = new String[]{}; break;
        }

        for (int i = 0; i < additionalFields.size(); i++) {
            String value = additionalFields.get(i).getText().trim();
            if (!value.isEmpty()) {
                if (additionalInfo.length() > 0) additionalInfo.append(", ");
                if (i < labels.length) {
                    additionalInfo.append(labels[i]).append(": ").append(value);
                } else {
                    additionalInfo.append(value);
                }
            }
        }

        rowData.add(additionalInfo.length() == 0 ? "N/A" : additionalInfo.toString());
    }

    public void process() {
        String records = "";
        for (int r = 0; r < model_Kendo.getRowCount(); r++) {
            for (int c = 0; c < model_Kendo.getColumnCount(); c++) {
                records += model_Kendo.getValueAt(r, c) + "#";
            }
            records += "\n";
        }
        db.storeToFile(records);
    }

    private void fillAdditionalFields(String type, String additionalInfo) {
        for (JTextField field : additionalFields) {
            field.setText("");
        }
        if (additionalInfo == null || additionalInfo.equals("N/A") || additionalInfo.trim().isEmpty()) return;

        String[] parts = additionalInfo.split(",\\s*");
        Map<String, String> infoMap = new HashMap<>();
        for (String part : parts) {
            String[] labelValue = part.split(":\\s*", 2);
            if (labelValue.length == 2) {
                infoMap.put(labelValue[0].trim(), labelValue[1].trim());
            }
        }

        String[] labels;
        switch (type) {
            case "Shinai": labels = new String[]{"Length", "Weight", "Material"}; break;
            case "Men": labels = new String[]{"Size", "Padding Type"}; break;
            case "Kote": labels = new String[]{"Glove Size"}; break;
            case "Dō": labels = new String[]{"Chest Size"}; break;
            case "Tare": labels = new String[]{"Waist Size"}; break;
            default: labels = new String[]{}; break;
        }

        for (int i = 0; i < labels.length && i < additionalFields.size(); i++) {
            String label = labels[i];
            if (infoMap.containsKey(label)) {
                additionalFields.get(i).setText(infoMap.get(label));
            }
        }
    }

    public static void main(String[] args) {
        new MainProgram();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnAdd)) {
            getData();
            model_Kendo.addRow(rowData);
            txtID.setText(getRowCount());
            resetComponents();
        } else if (e.getSource().equals(btnClear)) {
            resetComponents();
        } else if (e.getSource().equals(btnUpdate)) {
            int i = tblKendo.getSelectedRow();
            if (i >= 0) {
                getData();
                for (int col = 1; col < tblKendo.getColumnCount(); col++) {
                    tblKendo.setValueAt(rowData.get(col), i, col);
                }
                resetComponents();
            }
        } else if (e.getSource().equals(btnDelete)) {
            int i = tblKendo.getSelectedRow();
            if (i >= 0) {
                model_Kendo.removeRow(i);
                resetComponents();
            }
        } else if (e.getSource().equals(btnClose)) {
            process();
            dispose();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int i = tblKendo.getSelectedRow();
        if (i >= 0) {
            txtID.setText(tblKendo.getValueAt(i, 0).toString());
            String type = tblKendo.getValueAt(i, 1).toString();
            cboType.setSelectedItem(type);
            cboStatus.setSelectedItem(tblKendo.getValueAt(i, 2).toString());
            txtDate.setText(tblKendo.getValueAt(i, 3).toString());
            spinnerQuantity.setValue(Integer.parseInt(tblKendo.getValueAt(i, 4).toString()));
            cboCondition.setSelectedItem(tblKendo.getValueAt(i, 5).toString());
            txtLocation.setText(tblKendo.getValueAt(i, 6).toString());

            String addInfoStr = tblKendo.getValueAt(i, 7).toString();
            fillAdditionalFields(type, addInfoStr);

            tableClick();
        }
    }

    @Override public void mousePressed(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }
    @Override public void keyTyped(KeyEvent e) { }
    @Override public void keyPressed(KeyEvent e) { }
    @Override public void keyReleased(KeyEvent e) { }
    @Override public void windowOpened(WindowEvent e) { }
    @Override public void windowClosing(WindowEvent e) {
        process();
    }
    @Override public void windowClosed(WindowEvent e) { }
    @Override public void windowIconified(WindowEvent e) { }
    @Override public void windowDeiconified(WindowEvent e) { }
    @Override public void windowActivated(WindowEvent e) { }
    @Override public void windowDeactivated(WindowEvent e) { }
}
