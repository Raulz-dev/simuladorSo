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
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

    public Journal() {
        this.logEntries = new MyArrayList<>();
        this.dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
    }


    public void logOperation(String command, String details) {
        if (command == null || details == null) {
            System.err.println("Warning: Journal.logOperation chamado com command ou details nulo.");

            return;
        }

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
        if (filePath == null || filePath.trim().isEmpty()) {
            System.err.println("Error: File path for journal flushing cannot be null or empty.");
            return;
        }

        if (logEntries.isEmpty()) {
            System.out.println("Journal (in memory) is empty. Nothing to flush to " + filePath);
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            for (int i = 0; i < logEntries.size(); i++) {
                writer.write(logEntries.get(i));
                writer.newLine();
            }

            System.out.println("Journal flushed to " + filePath);
        } catch (IOException e) {
            System.err.println("Error flushing journal to file '" + filePath + "': " + e.getMessage());

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