import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Database {
    private File file;

    public Database() {}

    public Database(String filename) {
        this.file = new File(filename);
    }

    private void errorMessage(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void storeToFile(String records) {
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(records);
        } catch (IOException e) {
            errorMessage("Error writing to CSV file: " + e.getMessage());
        }
    }


    public void displayRecords(DefaultTableModel model) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Correct CSV splitting respecting quoted commas
                String[] data = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i].trim().replaceAll("^\"|\"$", "").replace("\"\"", "\"");
                }

                Vector<String> row = new Vector<>();
                for (int i = 0; i < model.getColumnCount() && i < data.length; i++) {
                    row.add(data[i]);
                }
                model.addRow(row);
            }
        } catch (IOException e) {
            errorMessage("Error reading CSV file: " + e.getMessage());
        }
    }


}
