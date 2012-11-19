package ru.fizteh.fivt.students.yushkevichAnton.parallelsort;

import java.io.*;
import java.util.*;

public class IOHandler {
    private BufferedReader bufferedReader;
    private PrintWriter    out;

    boolean     fileInput  = false;
    Queue<File> inputQueue = new LinkedList<File>();

    public IOHandler() {
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        out = new PrintWriter(System.out);
    }

    public void setOutputFile(String fileName) {
        try {
            out = new PrintWriter(new FileWriter(fileName));
        } catch (IOException e) {
            System.err.println("Output file not accessible.");
            out.close(); // sic!
            System.exit(1);
        }
    }

    public void addInputFile(String fileName) {
        fileInput = true;
        inputQueue.add(new File(fileName));

        openReader();
    }

    private void openReader() {
        try {
            bufferedReader = new BufferedReader(new FileReader(inputQueue.peek()));
        } catch (FileNotFoundException e) {
            System.err.println("Input file not found: " + inputQueue.peek().toString());
            System.exit(1);
        }
    }

    public void close() {
        try {
            bufferedReader.close();
        } catch (Exception e) {

        }
        out.flush();
        out.close();
    }

    public void println(String s) {
        out.println(s);
    }

    public String readLine() { // gvnkd
        String s = null;
        if (fileInput) {
            while (true) {
                try {
                    s = bufferedReader.readLine();
                } catch (IOException e) {
                    System.err.println("Unable to read from " + inputQueue.peek().toString());
                    System.exit(1);
                }
                if (s != null) {
                    return s;
                } else {
                    inputQueue.poll();
                    if (inputQueue.isEmpty()) {
                        return null;
                    }
                    openReader();
                }
            }
        } else {
            try {
                s = bufferedReader.readLine();
            } catch (IOException e) {
                System.err.println("Unable to read from cin.");
                System.exit(1);
            }
            return s;
        }
    }
}