import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.util.List;

public class PractitionerWindow extends JFrame implements ActionListener, WindowListener {
    private JButton btnAdd, btnDelete, btnClose, btnBack, btnEdit;
    private JPanel panelButton, panelTable, contentPanel;
    private JTable tblPractitioners;
    private DefaultTableModel model_Practitioners;
    private Vector<String> columns;
    private PractitionerDatabase db = new PractitionerDatabase();

    public PractitionerWindow() {
        setTitle("Practitioner List - DKC Inventory Manager");
        setSize(1000, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        btnAdd = new JButton("Add");
        btnDelete = new JButton("Delete");
        btnEdit = new JButton("Edit");
        btnClose = new JButton("Close");
        btnBack = new JButton("Back");

        btnAdd.addActionListener(this);
        btnDelete.addActionListener(this);
        btnEdit.addActionListener(this);
        btnClose.addActionListener(this);
        btnBack.addActionListener(this);

        JPanel panelRightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelRightButtons.add(btnAdd);
        panelRightButtons.add(btnEdit);
        panelRightButtons.add(btnDelete);
        panelRightButtons.add(btnClose);

        panelButton = new JPanel(new BorderLayout());
        panelButton.setBorder(BorderFactory.createTitledBorder("Actions"));
        panelButton.add(btnBack, BorderLayout.WEST);
        panelButton.add(panelRightButtons, BorderLayout.EAST);

        columns = new Vector<>();
        columns.add("ID");
        columns.add("Name");
        columns.add("Age");
        columns.add("Rank");
        columns.add("Date Started");
        columns.add("Days Attended");
        columns.add("Gear Sizes");
        columns.add("Eligible for Rank Up");

        model_Practitioners = new DefaultTableModel(columns, 0);
        tblPractitioners = new JTable(model_Practitioners);
        tblPractitioners.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tblPractitioners.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tblPractitioners.getSelectedRow() != -1) {
                    editSelectedPractitioner();
                }
            }
        });

        panelTable = new JPanel(new BorderLayout());
        panelTable.setBorder(BorderFactory.createTitledBorder("Practitioner List"));
        panelTable.add(new JScrollPane(tblPractitioners), BorderLayout.CENTER);

        contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.add(panelTable, BorderLayout.CENTER);
        contentPanel.add(panelButton, BorderLayout.SOUTH);

        add(contentPanel);

        List<String[]> records = db.loadAllPractitioners();
        for (String[] row : records) {
            if (row.length < 8) continue;
            Vector<String> fullRow = new Vector<>();
            for (int i = 0; i < 8; i++) {
                fullRow.add(row[i].trim().isEmpty() && i == 6 ? "N/A" : row[i].trim());
            }
            model_Practitioners.addRow(fullRow);
        }
    }

    public void addPractitioner(String name, int age, String rank, String startDate, String gearSizes) {
        String id = db.generateNextID();
        long daysAttended = db.calculateDaysAttended(startDate);
        String eligibility = db.checkRankUpEligibility(rank, daysAttended);

        Vector<Object> row = new Vector<>();
        row.add(id);
        row.add(name);
        row.add(age);
        row.add(rank);
        row.add(startDate);
        row.add(String.valueOf(daysAttended));
        row.add(gearSizes.isEmpty() ? "N/A" : gearSizes);
        row.add(eligibility);
        model_Practitioners.addRow(row);

        db.savePractitioner(id, name, age, rank, startDate, gearSizes);
    }

    public void updatePractitioner(String id, String name, int age, String rank, String startDate, String gearSizes, int rowIndex) {
        long daysAttended = db.calculateDaysAttended(startDate);
        String eligibility = db.checkRankUpEligibility(rank, daysAttended);

        model_Practitioners.setValueAt(id, rowIndex, 0);
        model_Practitioners.setValueAt(name, rowIndex, 1);
        model_Practitioners.setValueAt(String.valueOf(age), rowIndex, 2);
        model_Practitioners.setValueAt(rank, rowIndex, 3);
        model_Practitioners.setValueAt(startDate, rowIndex, 4);
        model_Practitioners.setValueAt(String.valueOf(daysAttended), rowIndex, 5);
        model_Practitioners.setValueAt(gearSizes.isEmpty() ? "N/A" : gearSizes, rowIndex, 6);
        model_Practitioners.setValueAt(eligibility, rowIndex, 7);

        db.updatePractitioner(id, name, String.valueOf(age), rank, startDate, gearSizes);
    }

    private void editSelectedPractitioner() {
        int selected = tblPractitioners.getSelectedRow();
        if (selected >= 0) {
            String id = model_Practitioners.getValueAt(selected, 0).toString();
            String name = model_Practitioners.getValueAt(selected, 1).toString();
            String age = model_Practitioners.getValueAt(selected, 2).toString();
            String rank = model_Practitioners.getValueAt(selected, 3).toString();
            String startDate = model_Practitioners.getValueAt(selected, 4).toString();
            String gearSizes = model_Practitioners.getValueAt(selected, 6).toString();
            new PractitionerLogin(this, id, name, age, rank, startDate, gearSizes, selected);
        } else {
            JOptionPane.showMessageDialog(this, "Select a practitioner to edit.");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            new PractitionerLogin(this).setVisible(true);
        } else if (e.getSource() == btnDelete) {
            int selected = tblPractitioners.getSelectedRow();
            if (selected >= 0) {
                String idToDelete = model_Practitioners.getValueAt(selected, 0).toString();
                db.deletePractitionerByID(idToDelete);
                model_Practitioners.removeRow(selected);
            } else {
                JOptionPane.showMessageDialog(this, "Select a row to delete.");
            }
        } else if (e.getSource() == btnEdit) {
            editSelectedPractitioner();
        } else if (e.getSource() == btnClose) {
            this.dispose();
        } else if (e.getSource() == btnBack) {
            this.dispose();
            new MainProgram();
        }
    }

    public static void main(String[] args) {
        new PractitionerWindow();
    }

    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowClosing(WindowEvent e) {}
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}
}