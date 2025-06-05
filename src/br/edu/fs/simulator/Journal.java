package br.edu.fs.simulator;

import br.edu.fs.simulator.util.MyArrayList;
import br.edu.fs.simulator.util.MyStringBuilder;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Journal {
    private MyArrayList<String> logEntries;
    private SimpleDateFormat dateFormat;

    public Journal() {
        this.logEntries = new MyArrayList<>();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    }

    public void logOperation(String command, String details) {
        MyStringBuilder entryBuilder = new MyStringBuilder();
        entryBuilder.append("[");
        entryBuilder.append(dateFormat.format(new Date(System.currentTimeMillis())));
        entryBuilder.append("] ");
        entryBuilder.append(command.toUpperCase());
        entryBuilder.append(": ");
        entryBuilder.append(details);
        logEntries.add(entryBuilder.toString());
    }

    public void flushToFile(String filePath) {
        if (logEntries.isEmpty()) {
            System.out.println("Journal is empty. Nothing to flush.");
            return;
        }

        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(filePath, true));
            for (int i = 0; i < logEntries.size(); i++) {
                writer.write(logEntries.get(i));
                writer.newLine();
            }
            logEntries.clear();
            System.out.println("Journal flushed to " + filePath);
        } catch (IOException e) {
            System.err.println("Error flushing journal to file: " + e.getMessage());
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing journal file writer: " + e.getMessage());
            }
        }
    }

    public void printLog() {
        if (logEntries.isEmpty()) {
            System.out.println("Journal is currently empty (in memory).");
            return;
        }
        System.out.println("--- Journal Entries (In Memory) ---");
        for (int i = 0; i < logEntries.size(); i++) {
            System.out.println(logEntries.get(i));
        }
        System.out.println("----------------------------------");
    }
}