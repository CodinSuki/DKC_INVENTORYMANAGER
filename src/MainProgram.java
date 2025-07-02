    //Kyle Siton

    //GUI handler
    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;
    import com.github.lgooddatepicker.components.DatePicker;
    import com.github.lgooddatepicker.components.DatePickerSettings;
    import java.awt.*;
    import java.awt.event.*;
    //File Handling
    import java.io.File;
    import java.io.IOException;
    //Util
    import java.time.LocalDate;
    import java.time.format.DateTimeFormatter;
    import java.util.*;




    public class MainProgram extends MainFrame implements ActionListener, MouseListener, KeyListener, WindowListener {

        private JLabel lblID, lblType, lblStatus, lblDate, lblQuantity, lblCondition, lblLocation;
        private JTextField txtID, txtLocation, txtBorrowerName;
        private JComboBox<String> cboType, cboStatus, cboCondition;
        private JSpinner spinnerQuantity;
        private JButton btnAdd, btnClear, btnUpdate, btnDelete, btnClose, btnFilter, btnBorrower, btnPractitioner;
        private JPanel panelButton, panelTable, panelKendoInfo, panelDynamicFields;

        private DatePicker datePickerChooser;

        private Database db = new Database("kendo_inventory.csv");

        private JTable tblKendo;
        private DefaultTableModel model_Kendo;
        private Vector columns, rowData;
        private java.util.List<JTextField> additionalFields = new ArrayList<>();

        public MainProgram() {
            ImageIcon icon = new ImageIcon("src/IMAGES/logo_davao.png");
            setIconImage(icon.getImage());
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

            JPanel accessButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            accessButtons.add(btnBorrower);
            accessButtons.add(btnPractitioner);
            panelButton.add(accessButtons);

            btnAdd.addActionListener(this);
            btnClear.addActionListener(this);
            btnUpdate.addActionListener(this);
            btnDelete.addActionListener(this);
            btnClose.addActionListener(this);
            btnFilter.addActionListener(this);
            btnBorrower.addActionListener(this);
            btnPractitioner.addActionListener(this);

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

            txtID = new JTextField(20);
            txtID.setEditable(false);

            DatePickerSettings settings = new DatePickerSettings();
            settings.setFormatForDatesCommonEra("dd/MM/yyyy");
            datePickerChooser = new DatePicker(settings);
            datePickerChooser.setDate(LocalDate.now());

            txtLocation = new JTextField(15);

            cboType = new JComboBox<>(new String[]{"Select Type", "Shinai", "Men", "Kote", "Dō", "Tare"});
            cboStatus = new JComboBox<>(new String[]{"Available", "In Repair", "Unavailable"});
            cboCondition = new JComboBox<>(new String[]{"New", "Good", "Fair", "Damaged"});

            spinnerQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

            cboType.addActionListener(e -> {
                String selectedType = (String) cboType.getSelectedItem();
                updateTypeSpecificFields(selectedType);
            });
        }

        public void KendoInfo() {
            panelKendoInfo = new JPanel(new GridLayout(7, 2, 5, 5));
            panelKendoInfo.setBorder(BorderFactory.createTitledBorder("Kendo Equipment Registration"));
            panelKendoInfo.setMaximumSize(new Dimension(400, 300));

            panelKendoInfo.add(lblID);panelKendoInfo.add(txtID);panelKendoInfo.add(lblType);panelKendoInfo.add(cboType);
            panelKendoInfo.add(lblStatus);panelKendoInfo.add(cboStatus);panelKendoInfo.add(lblDate);
            panelKendoInfo.add(datePickerChooser);panelKendoInfo.add(lblQuantity);panelKendoInfo.add(spinnerQuantity);
            panelKendoInfo.add(lblCondition);panelKendoInfo.add(cboCondition);panelKendoInfo.add(lblLocation);
            panelKendoInfo.add(txtLocation);

            panelDynamicFields = new JPanel(new GridLayout(5, 2, 5, 5));
            panelDynamicFields.setBorder(BorderFactory.createTitledBorder("Additional Info"));
            panelDynamicFields.setMaximumSize(new Dimension(400, 200));
        }

        public void panelKendoButton() {


            panelButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            panelButton.setBorder(BorderFactory.createTitledBorder("Actions"));

            btnAdd = new JButton("Add");
            btnClear = new JButton("Clear");
            btnUpdate = new JButton("Update");
            btnDelete = new JButton("Delete");
            btnClose = new JButton("Close");
            btnFilter = new JButton("Filter");
            btnBorrower = new JButton("Borrower Manager");
            btnPractitioner = new JButton("Practitioner Manager");


            panelButton.add(btnAdd);panelButton.add(btnClear);
            panelButton.add(btnUpdate);panelButton.add(btnDelete);
            panelButton.add(btnClose);panelButton.add(btnFilter);


        }

        public void resetComponents() {
            txtID.setText(getRowCount());
            btnAdd.setEnabled(true);
            btnClear.setEnabled(true);
            btnClose.setEnabled(true);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
            btnFilter.setEnabled(true);

            txtLocation.setText("");
            datePickerChooser.setDate(LocalDate.now());
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

            LocalDate date = datePickerChooser.getDate();
            rowData.add(date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A");

            rowData.add(spinnerQuantity.getValue().toString());
            rowData.add(cboCondition.getSelectedItem());
            rowData.add(txtLocation.getText());

            StringBuilder additionalInfo = new StringBuilder();
            String selectedType = (String) cboType.getSelectedItem();
            String[] labels;

            switch (selectedType) {
                case "Shinai":
                    labels = new String[]{"Length", "Weight", "Material"};
                    break;
                case "Men":
                    labels = new String[]{"Size", "Padding Type"};
                    break;
                case "Kote":
                    labels = new String[]{"Glove Size"};
                    break;
                case "Dō":
                    labels = new String[]{"Chest Size"};
                    break;
                case "Tare":
                    labels = new String[]{"Waist Size"};
                    break;
                default:
                    labels = new String[]{};
                    break;
            }


            for (int i = 0; i < additionalFields.size(); i++) {
                String value = additionalFields.get(i).getText().trim();
                if (!value.isEmpty()) {
                    if (additionalInfo.length() > 0) additionalInfo.append(", ");

                    if  (i < labels.length) {
                        additionalInfo.append(labels[i]).append(": ").append(value);
                    } else {
                        additionalInfo.append(value);
                    }
                }
            }

            rowData.add(additionalInfo.length() == 0 ? "N/A" : additionalInfo.toString());
        }

        public void process() {
            StringBuilder records = new StringBuilder();
            for (int r = 0; r < model_Kendo.getRowCount(); r++) {
                for (int c = 0; c < model_Kendo.getColumnCount(); c++) {
                    String cell = model_Kendo.getValueAt(r, c).toString()
                            .replace("\"", "\"\"");
                    records.append("\"").append(cell).append("\"");
                    if (c < model_Kendo.getColumnCount() - 1)
                        records.append(",");
                }
                records.append("\n");
            }
            db.storeToFile(records.toString());
        }


        public void reloadTable() {
            System.out.println("Reloading main program table from MainPrg...");
            model_Kendo.setRowCount(0);

            try (Scanner sc = new Scanner(new File("kendo_inventory.csv"))) {
                while (sc.hasNextLine()) {
                    String[] r = sc.nextLine().split(",", -1);
                    if (r.length >= 8) {
                        model_Kendo.addRow(new Object[]{
                                r[0].replace("\"", "").trim(), // ID
                                r[1].replace("\"", "").trim(), // Type
                                r[2].replace("\"", "").trim(), // Status
                                r[3].replace("\"", "").trim(), // Date Acquired
                                r[4].replace("\"", "").trim(), // Quantity
                                r[5].replace("\"", "").trim(), // Condition
                                r[6].replace("\"", "").trim(), // Location
                                r[7].replace("\"", "").trim()  // Additional Info
                        });
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        private void fillAdditionalFields(String type, String additionalInfo) {
            for (JTextField field : additionalFields) field.setText("");
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
                case "Shinai":
                    labels = new String[]{"Length", "Weight", "Material"};
                    break;
                case "Men":
                    labels = new String[]{"Size", "Padding Type"};
                    break;
                case "Kote":
                    labels = new String[]{"Glove Size"};
                    break;
                case "Dō":
                    labels = new String[]{"Chest Size"};
                    break;
                case "Tare":
                    labels = new String[]{"Waist Size"};
                    break;
                default:
                    labels = new String[]{};
                    break;
            }

            for (int i = 0; i < labels.length && i < additionalFields.size(); i++) {
                if (infoMap.containsKey(labels[i])) {
                    additionalFields.get(i).setText(infoMap.get(labels[i]));
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
            } else if (e.getSource().equals(btnFilter)) {
                new FilterWindow(this, model_Kendo);
            } else if (e.getSource().equals(btnBorrower)) {
                process();
                dispose();
                BorrowerWindow bw = new BorrowerWindow(this);
                bw.setVisible(true);
        } else if (e.getSource().equals(btnPractitioner)) {
                process();
                dispose();
                new PractitionerWindow();
            }
        }


        @Override
        public void mouseClicked(MouseEvent e) {
            int i = tblKendo.getSelectedRow();
            if (i >= 0) {
                txtID.setText(model_Kendo.getValueAt(i, 0).toString());
                cboType.setSelectedItem(model_Kendo.getValueAt(i, 1));
                cboStatus.setSelectedItem(model_Kendo.getValueAt(i, 2));

                String dateStr = model_Kendo.getValueAt(i, 3).toString();
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    datePickerChooser.setDate(LocalDate.parse(dateStr, formatter));
                } catch (Exception ex) {
                    datePickerChooser.setDate(null);
                }

                spinnerQuantity.setValue(Integer.parseInt(model_Kendo.getValueAt(i, 4).toString()));
                cboCondition.setSelectedItem(model_Kendo.getValueAt(i, 5));
                txtLocation.setText(model_Kendo.getValueAt(i, 6).toString());

                String addInfo = model_Kendo.getValueAt(i, 7).toString();
                updateTypeSpecificFields((String) cboType.getSelectedItem());
                fillAdditionalFields((String) cboType.getSelectedItem(), addInfo);

                tableClick();
            }
        }

        //Mouseys


        @Override public void mousePressed(MouseEvent e) {}
        @Override public void mouseReleased(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}

        // KeyListener
        @Override public void keyTyped(KeyEvent e) {}
        @Override public void keyPressed(KeyEvent e) {}
        @Override public void keyReleased(KeyEvent e) {}

        // WindowListener
        @Override
        public void windowClosing(WindowEvent e) {
            process();
            System.exit(0);
        }
        @Override public void windowOpened(WindowEvent e) {}
        @Override public void windowClosed(WindowEvent e) {}
        @Override public void windowIconified(WindowEvent e) {}
        @Override public void windowDeiconified(WindowEvent e) {}
        @Override public void windowActivated(WindowEvent e) {
        }
        @Override public void windowDeactivated(WindowEvent e) {}
    }
