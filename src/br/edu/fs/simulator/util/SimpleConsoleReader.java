package br.edu.fs.simulator.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class SimpleConsoleReader {
    private BufferedReader reader;

    public SimpleConsoleReader() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    public String readLine() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
            return null;
        }
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            System.err.println("Error closing reader: " + e.getMessage());
        }
    }
}