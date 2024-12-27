package org.example.models;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private static final String HISTORY_FILE = "history.txt";

    public static void saveHistory(History history) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE, true))) {
            writer.write(history.getTransferType() + "," + history.getFileName() + "," +
                    history.getFilePath() + "," + history.getTimestamp());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving history: " + e.getMessage());
        }
    }

    public static List<History> loadHistory() {
        List<History> historyList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HISTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    historyList.add(new History(parts[1], parts[2], parts[0], parts[3]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading history: " + e.getMessage());
        }
        return historyList;
    }
}
