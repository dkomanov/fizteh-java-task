package ru.fizteh.fivt.students.harius.sort;

import java.io.*;
import java.util.List;

public abstract class LinesInputFactory {
    public static LinesInput create(List<List<String>> q, int index) {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        return new LinesInput(stdin, q, index);
    }

    public static LinesInput create(String filename, List<List<String>> q, int index)
        throws IOException {

        BufferedReader fin = new BufferedReader(new FileReader(filename));
        return new LinesInput(fin, q, index);
    }
}