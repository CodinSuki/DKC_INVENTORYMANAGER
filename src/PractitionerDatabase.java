import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class PractitionerDatabase {
    private static final String FILE_PATH = "practitioners.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PractitionerDatabase() {
        ensureFileExists();
    }

    private void ensureFileExists() {
        if (!Files.exists(Paths.get(FILE_PATH))) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
                writer.println("ID,Name,Age,Rank,Date Started,Days Attended,Gear Sizes,Eligible for Rank Up");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String generateNextID() {
        int maxNum = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts[0].startsWith("A")) {
                    int num = Integer.parseInt(parts[0].substring(1));
                    maxNum = Math.max(maxNum, num);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "A" + (maxNum + 1);
    }

    public void savePractitioner(String id, String name, int age, String rank, String startDate, String gearSizes) {
        long daysAttended = calculateDaysAttended(startDate);
        String eligibility = checkRankUpEligibility(rank, daysAttended);

        // Escape gearSizes and other fields that might contain commas
        String escapedGearSizes = escapeCommas(gearSizes);
        String entry = String.join(",",
                id,
                escapeCommas(name),
                String.valueOf(age),
                escapeCommas(rank),
                startDate,
                String.valueOf(daysAttended),
                escapedGearSizes,
                eligibility
        );

        try (FileWriter fw = new FileWriter(FILE_PATH, true)) {
            fw.write(entry + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public List<String[]> loadAllPractitioners() {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] row = parseCSVLine(line);
                    if (row.length < 8) {
                        row = Arrays.copyOf(row, 8);
                        for (int i = 0; i < 8; i++) {
                            if (row[i] == null) row[i] = "N/A";
                        }
                    }
                    data.add(row);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    protected long calculateDaysAttended(String dateStr) {
        try {
            LocalDate start = LocalDate.parse(dateStr, FORMATTER);
            return ChronoUnit.DAYS.between(start, LocalDate.now());
        } catch (Exception e) {
            return 0;
        }
    }

    protected String checkRankUpEligibility(String rank, long daysAttended) {
        switch (rank.toLowerCase()) {
            case "beginner": return daysAttended >= 90 ? "Yes" : "No";
            case "intermediate": return daysAttended >= 180 ? "Yes" : "No";
            case "advanced": return daysAttended >= 365 ? "Yes" : "No";
            default: return "N/A";
        }
    }

    public void deletePractitionerByID(String id) {
        List<String[]> allRecords = loadAllPractitioners();
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            writer.println("ID,Name,Age,Rank,Date Started,Days Attended,Gear Sizes,Eligible for Rank Up");
            for (String[] record : allRecords) {
                if (!record[0].equals(id)) {
                    writer.println(String.join(",", record));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String escapeCommas(String value) {
        if (value.contains(",")) {
            return '"' + value.replace("\"", "\"\"") + '"';
        }
        return value;
    }

    private String[] parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        tokens.add(sb.toString().trim());

        return tokens.toArray(new String[0]);
    }
}
