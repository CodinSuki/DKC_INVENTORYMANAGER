import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class FilterWindow extends JDialog {
    private JComboBox<String> cboTypeFilter, cboStatusFilter, cboConditionFilter;
    private JButton btnApply, btnReset;
    private DefaultTableModel originalModel;
    private DefaultTableModel model_Kendo;

    public FilterWindow(JFrame parent, DefaultTableModel model_Kendo) {
        super(parent, "Filter Kendo Equipment", true);
        this.model_Kendo = model_Kendo;


        originalModel = new DefaultTableModel();
        for (int i = 0; i < model_Kendo.getColumnCount(); i++) {
            originalModel.addColumn(model_Kendo.getColumnName(i));
        }
        for (int i = 0; i < model_Kendo.getRowCount(); i++) {
            Vector row = new Vector();
            for (int j = 0; j < model_Kendo.getColumnCount(); j++) {
                row.add(model_Kendo.getValueAt(i, j));
            }
            originalModel.addRow(row);
        }

        cboTypeFilter = new JComboBox<>(new String[]{"All", "Shinai", "Men", "Kote", "Dō", "Tare"});
        cboStatusFilter = new JComboBox<>(new String[]{"All", "Available", "Borrowed", "In Repair", "Unavailable"});
        cboConditionFilter = new JComboBox<>(new String[]{"All", "New", "Good", "Fair", "Damaged"});

        btnApply = new JButton("Apply Filter");
        btnReset = new JButton("Close & Reset");

        btnApply.addActionListener(e -> applyFilter());
        btnReset.addActionListener(e -> {
            restoreOriginalData();
            dispose();
        });

        setLayout(new GridLayout(5, 2, 10, 10));
        add(new JLabel("Filter by Type:")); add(cboTypeFilter);
        add(new JLabel("Filter by Status:")); add(cboStatusFilter);
        add(new JLabel("Filter by Condition:")); add(cboConditionFilter);
        add(btnApply); add(btnReset);

        setSize(300, 200);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void applyFilter() {
        String selectedType = (String) cboTypeFilter.getSelectedItem();
        String selectedStatus = (String) cboStatusFilter.getSelectedItem();
        String selectedCondition = (String) cboConditionFilter.getSelectedItem();

        model_Kendo.setRowCount(0); // clear table

        for (int i = 0; i < originalModel.getRowCount(); i++) {
            boolean matches = true;

            if (!selectedType.equals("All") && !selectedType.equals(originalModel.getValueAt(i, 1)))
                matches = false;
            if (!selectedStatus.equals("All") && !selectedStatus.equals(originalModel.getValueAt(i, 2)))
                matches = false;
            if (!selectedCondition.equals("All") && !selectedCondition.equals(originalModel.getValueAt(i, 5)))
                matches = false;

            if (matches) {
                Vector row = new Vector();
                for (int j = 0; j < originalModel.getColumnCount(); j++) {
                    row.add(originalModel.getValueAt(i, j));
                }
                model_Kendo.addRow(row);
            }
        }
    }

    private void restoreOriginalData() {
        model_Kendo.setRowCount(0);
        for (int i = 0; i < originalModel.getRowCount(); i++) {
            Vector row = new Vector();
            for (int j = 0; j < originalModel.getColumnCount(); j++) {
                row.add(originalModel.getValueAt(i, j));
            }
            model_Kendo.addRow(row);
        }
    }
}

