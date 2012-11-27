package ru.fizteh.fivt.students.harius.sort;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class LinesInput implements Runnable {
    private BufferedReader input;
    private List<List<String>> q;
    private int index;

    public LinesInput(BufferedReader input, List<List<String>> q, int index) {
        this.input = input;
        this.q = q;
        this.index = index;
    }

    @Override
    public void run() {
        List<String> result = new ArrayList<String>();
        try {
            while (true) {
                String line = input.readLine();
                if (line == null) {
                    break;
                }
                result.add(line);
            }
        } catch (IOException ioEX) {
            result = null;
        } finally {
            try {
                input.close();
            } catch (IOException ioEx) {
                result = null;
            }
        }
        q.set(index, result);
    }
}