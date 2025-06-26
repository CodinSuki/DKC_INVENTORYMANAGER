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

    public void setFilename(String filename) {
        this.file = new File(filename);
    }

    public String getFilename() {
        return file.getName();
    }

    private void errorMessage(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Store information to a CSV file
     * @param records - the information to be stored
     */
    public void storeToFile(String records) {
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(records);
        } catch (IOException e) {
            errorMessage("Error writing to CSV file: " + e.getMessage());
        }
    }

    /**
     * Display records from CSV into the table model
     * @param model - table model to populate
     */
    public void displayRecords(DefaultTableModel model) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1); // keep empty fields
                Vector<String> row = new Vector<>();
                for (int i = 0; i < model.getColumnCount() && i < data.length; i++) {
                    row.add(data[i].trim().replaceAll("^\"|\"$", "").replace("\"\"", "\""));
                }
                model.addRow(row);
            }
        } catch (IOException e) {
            errorMessage("Error reading CSV file: " + e.getMessage());
        }
    }

    public List<String> loadColumn(String filename, int colIndex) {
        List<String> data = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(filename))) {
            while (sc.hasNextLine()) {
                String[] parts = sc.nextLine().split(",");
                if (parts.length > colIndex) {
                    String value = parts[colIndex].replace("\"","").trim();
                    data.add(value);
                }
            }
        } catch (IOException e) {
            errorMessage("DB error: " + e.getMessage());
        }
        return data;
    }


} // end of class
