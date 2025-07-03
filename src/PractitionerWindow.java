import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.List;

public class PractitionerWindow extends JFrame implements ActionListener, WindowListener {
    private JButton btnAdd, btnDelete, btnClose, btnBack, btnEdit, btnAttended;
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
        btnAdd = new JButton("Add");btnDelete = new JButton("Delete");
        btnEdit = new JButton("Edit");btnClose = new JButton("Close");
        btnBack = new JButton("Back");btnAttended = new JButton("Attended");

        btnAttended.addActionListener(this);btnAdd.addActionListener(this);
        btnDelete.addActionListener(this);btnEdit.addActionListener(this);
        btnClose.addActionListener(this);btnBack.addActionListener(this);

        JPanel panelRightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelRightButtons.add(btnAttended);panelRightButtons.add(btnAdd);
        panelRightButtons.add(btnEdit);panelRightButtons.add(btnDelete);
        panelRightButtons.add(btnClose);


        panelButton = new JPanel(new BorderLayout());
        panelButton.setBorder(BorderFactory.createTitledBorder("Actions"));
        panelButton.add(btnBack, BorderLayout.WEST);
        panelButton.add(panelRightButtons, BorderLayout.EAST);

        columns = new Vector<>();
        columns.add("ID");columns.add("Name");columns.add("Age");columns.add("Rank");columns.add("Date Started");
        columns.add("Days Attended");columns.add("Gear Sizes");columns.add("Eligible for Rank Up");

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

            String id = row[0].trim();
            String name = row[1].trim();
            String age = row[2].trim();
            String rank = row[3].trim();
            String startDate = row[4].trim();
            String gearSizes = row[6].trim().isEmpty() ? "N/A" : row[6].trim();

            long daysAttended = 0;

            try {
                daysAttended = Long.parseLong(row[5].trim());
            } catch (NumberFormatException ignored) {}
            String eligibility = db.checkRankUpEligibility(rank, daysAttended);

            Vector<String> fullRow = new Vector<>();
            fullRow.add(id);
            fullRow.add(name);
            fullRow.add(age);
            fullRow.add(rank);
            fullRow.add(startDate);
            fullRow.add(String.valueOf(daysAttended));
            fullRow.add(gearSizes);
            fullRow.add(eligibility);

            model_Practitioners.addRow(fullRow);
        }

    }

    public void SetAttended(){
        int selected = tblPractitioners.getSelectedRow();
        if (selected >= 0) {
            String id = model_Practitioners.getValueAt(selected, 0).toString();
            String daysStr = model_Practitioners.getValueAt(selected, 5).toString();
            long days = 0;
            try {
                days = Long.parseLong(daysStr);
            } catch (NumberFormatException ignored) {}
            days++;

            String rank = model_Practitioners.getValueAt(selected, 3).toString();
            String eligibility = db.checkRankUpEligibility(rank, days);

            model_Practitioners.setValueAt(String.valueOf(days), selected, 5);
            model_Practitioners.setValueAt(eligibility, selected, 7);

            db.incrementAttendanceByID(id);
        } else {
            JOptionPane.showMessageDialog(this, "Select a practitioner first.");
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

    public void process() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("practitioners.csv"))) {
            writer.println("ID,Name,Age,Rank,Date Started,Days Attended,Gear Sizes,Eligible for Rank Up");
            for (int r = 0; r < model_Practitioners.getRowCount(); r++) {
                StringBuilder sb = new StringBuilder();
                for (int c = 0; c < model_Practitioners.getColumnCount(); c++) {
                    String cell = model_Practitioners.getValueAt(r, c).toString();
                    if (cell.contains(",")) {
                        cell = "\"" + cell.replace("\"", "\"\"") + "\"";
                    }
                    sb.append(cell);
                    if (c < model_Practitioners.getColumnCount() - 1) {
                        sb.append(",");
                    }
                }
                writer.println(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        } else if (e.getSource() == btnAttended){
            SetAttended();
        } else if (e.getSource() == btnEdit) {
            editSelectedPractitioner();
        } else if (e.getSource() == btnClose) {
            process();
            this.dispose();
        } else if (e.getSource() == btnBack) {
            process();
            this.dispose();
            new MainProgram();
        }
    }

    public static void main(String[] args) {
        new PractitionerWindow();
    }

    @Override public void windowOpened(WindowEvent e) {}
    @Override public void windowClosing(WindowEvent e) {
        process();
        dispose();
    }
    @Override public void windowClosed(WindowEvent e) {}
    @Override public void windowIconified(WindowEvent e) {}
    @Override public void windowDeiconified(WindowEvent e) {}
    @Override public void windowActivated(WindowEvent e) {}
    @Override public void windowDeactivated(WindowEvent e) {}
}