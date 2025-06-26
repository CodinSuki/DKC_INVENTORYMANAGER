import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Vector;
import java.util.List;

public class PractitionerWindow extends JFrame implements ActionListener, WindowListener {
    private JButton btnAdd, btnDelete, btnClose, btnBack;
    private JPanel panelButton, panelTable, contentPanel;
    private JTable tblPractitioners;
    private DefaultTableModel model_Practitioners;
    private Vector<String> columns;
    private PractitionerDatabase db = new PractitionerDatabase();

    private int practitionerCount = 1;

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
        btnClose = new JButton("Close");
        btnBack = new JButton("Back");

        btnAdd.addActionListener(this);
        btnDelete.addActionListener(this);
        btnClose.addActionListener(this);
        btnBack.addActionListener(this);

        JPanel panelRightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelRightButtons.add(btnAdd);
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
            Vector<String> fullRow = new Vector<>();
            for (String cell : row) fullRow.add(cell.trim());
            while (fullRow.size() < columns.size()) fullRow.add("N/A");
            model_Practitioners.addRow(fullRow);
        }

        tblPractitioners.revalidate();
        tblPractitioners.repaint();
    }

    private String checkRankUpEligibility(String rank, long daysAttended) {
        switch (rank.toLowerCase()) {
            case "beginner": return daysAttended >= 90 ? "Yes" : "No";
            case "intermediate": return daysAttended >= 180 ? "Yes" : "No";
            case "advanced": return daysAttended >= 365 ? "Yes" : "No";
            default: return "N/A";
        }
    }

    public void addPractitioner(String name, int age, String rank, String startDate, String gearSizes) {
        String id = db.generateNextID();
        long daysAttended = ChronoUnit.DAYS.between(LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")), LocalDate.now());
        String eligibility = checkRankUpEligibility(rank, daysAttended);

        Vector<Object> row = new Vector<>();
        row.add(id);
        row.add(name);
        row.add(age);
        row.add(rank);
        row.add(startDate);
        row.add(daysAttended);
        row.add(gearSizes.isEmpty() ? "N/A" : gearSizes);
        row.add(eligibility);
        model_Practitioners.addRow(row);

        db.savePractitioner(id, name, age, rank, startDate, gearSizes);
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
