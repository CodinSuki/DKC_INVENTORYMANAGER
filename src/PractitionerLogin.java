import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PractitionerLogin extends JFrame implements ActionListener {
    private JTextField txtName, txtAge;
    private JComboBox<String> cboRank;
    private JTextField txtMenSize, txtKoteSize, txtDoSize, txtTareSize;
    private DatePicker datePicker;
    private JButton btnConfirm, btnCancel;
    private PractitionerWindow parent;
    private boolean isEditMode = false;
    private String editPractitionerID;
    private int editRowIndex;

    public PractitionerLogin(PractitionerWindow parent) {
        this.parent = parent;
        setTitle("Add Practitioner");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
        setVisible(true);
    }

    public PractitionerLogin(PractitionerWindow parent, String id, String name, String age, String rank, String startDate, String gearSizes, int rowIndex) {
        this.parent = parent;
        this.isEditMode = true;
        this.editPractitionerID = id;
        this.editRowIndex = rowIndex;
        setTitle("Edit Practitioner");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
        txtName.setText(name);
        txtAge.setText(age);
        cboRank.setSelectedItem(rank);
        datePicker.setDate(LocalDate.parse(startDate, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtMenSize.setText(extractGearSize(gearSizes, "Men"));
        txtKoteSize.setText(extractGearSize(gearSizes, "Kote"));
        txtDoSize.setText(extractGearSize(gearSizes, "Dō"));
        txtTareSize.setText(extractGearSize(gearSizes, "Tare"));
        setVisible(true);
    }

    private String extractGearSize(String gearSizes, String gearType) {
        String[] parts = gearSizes.split(",");
        for (String part : parts) {
            if (part.trim().startsWith(gearType)) {
                return part.split(":")[1].trim();
            }
        }
        return "";
    }

    private void initComponents() {
        JLabel lblName = new JLabel("Name:");
        JLabel lblAge = new JLabel("Age:");
        JLabel lblRank = new JLabel("Rank:");
        JLabel lblDate = new JLabel("Date Started:");
        JLabel lblMen = new JLabel("Men Size:");
        JLabel lblKote = new JLabel("Kote Size:");
        JLabel lblDo = new JLabel("Dō Size:");
        JLabel lblTare = new JLabel("Tare Size:");

        txtName = new JTextField(20);
        txtAge = new JTextField(5);
        cboRank = new JComboBox<>(new String[]{"Mudansha", "1st Dan", "2nd Dan", "3rd Dan", "4th Dan", "5th Dan"});
        txtMenSize = new JTextField(10);
        txtKoteSize = new JTextField(10);
        txtDoSize = new JTextField(10);
        txtTareSize = new JTextField(10);

        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra("dd/MM/yyyy");
        datePicker = new DatePicker(settings);
        datePicker.setDateToToday();

        btnConfirm = new JButton("Confirm");
        btnCancel = new JButton("Cancel");

        btnConfirm.addActionListener(this);
        btnCancel.addActionListener(this);

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(lblName); panel.add(txtName);
        panel.add(lblAge); panel.add(txtAge);
        panel.add(lblRank); panel.add(cboRank);
        panel.add(lblDate); panel.add(datePicker);
        panel.add(lblMen); panel.add(txtMenSize);
        panel.add(lblKote); panel.add(txtKoteSize);
        panel.add(lblDo); panel.add(txtDoSize);
        panel.add(lblTare); panel.add(txtTareSize);
        panel.add(btnConfirm); panel.add(btnCancel);

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnConfirm) {
            String name = txtName.getText().trim();
            String ageStr = txtAge.getText().trim();
            String rank = cboRank.getSelectedItem().toString();
            String men = txtMenSize.getText().trim();
            String kote = txtKoteSize.getText().trim();
            String dō = txtDoSize.getText().trim();
            String tare = txtTareSize.getText().trim();

            if (name.isEmpty() || ageStr.isEmpty() || rank.isEmpty() || datePicker.getDate() == null
                    || men.isEmpty() || kote.isEmpty() || dō.isEmpty() || tare.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            try {
                int age = Integer.parseInt(ageStr);
                String startDate = datePicker.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                String gearSizes = "Men: " + men + ", Kote: " + kote + ", Dō: " + dō + ", Tare: " + tare;

                if (isEditMode) {
                    parent.updatePractitioner(editPractitionerID, name, age, rank, startDate, gearSizes, editRowIndex);
                } else {
                    parent.addPractitioner(name, age, rank, startDate, gearSizes);
                }
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid age.");
            }
        } else if (e.getSource() == btnCancel) {
            dispose();
        }
    }
}
