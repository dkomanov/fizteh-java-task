package ru.fizteh.fivt.students.harius.sort;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class LinesInput implements Callable<List<String>> {
    private BufferedReader input;

    public LinesInput(BufferedReader input) {
        this.input = input;
    }

    @Override
    public List<String> call() throws IOException {
        List<String> result = new ArrayList<String>();
        try {
            while (true) {
                String line = input.readLine();
                if (line == null) {
                    break;
                }
                result.add(line);
            }
        } finally {
            input.close();
        }
        return result;
    }
}