import java.io.*;
import javax.swing.table.DefaultTableModel;

public class BorrowerDatabase {
    private static final String LOG_FILE = "borrow_log.csv";

    public void loadActiveBorrows(DefaultTableModel model) {
        try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 5) {
                    model.addRow(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void updateInventoryStatus(String itemName, String newStatus) {
        File inventoryFile = new File("kendo_inventory.csv");
        File tempFile = new File("temp_inventory.csv");

        try (BufferedReader reader = new BufferedReader(new FileReader(inventoryFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);

                // Update the status field (index 2) if item matches (assumes index 1 is Type or Name)
                if (parts.length >= 3 && parts[1].replace("\"", "").trim().equalsIgnoreCase(itemName)) {
                    parts[2] = "\"" + newStatus + "\""; // index 2 = status
                }

                // Reconstruct line and write to temp file
                writer.println(String.join(",", parts));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Replace old file with new
        if (!inventoryFile.delete() || !tempFile.renameTo(inventoryFile)) {
            System.err.println("Failed to update inventory status.");
        }
    }


    public void saveBorrow(String practitioner, String item, int qty, String borrowDate, String returnDate) {
        String entry = String.join(",", practitioner, item, String.valueOf(qty), borrowDate, returnDate);
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write(entry + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateInventoryStatus(item, "Borrowed");
    }


    public void removeBorrow(String practitioner, String item, int qty, String date, String returnDate) {
        File inputFile = new File(LOG_FILE);
        File tempFile = new File("temp_borrow_log.csv");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);
                if (data.length >= 5 &&
                        data[0].equals(practitioner) &&
                        data[1].equals(item) &&
                        data[2].equals(String.valueOf(qty)) &&
                        data[3].equals(date) &&
                        data[4].equals(returnDate)) {
                    continue; // skip this line
                }
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
            System.err.println("Failed to update borrow log.");
        }
    }
}
