package ru.fizteh.fivt.students.harius.sort;

import java.io.*;

public abstract class LinesInputFactory {
    public static LinesInput create() {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        return new LinesInput(stdin);
    }

    public static LinesInput create(String filename) throws IOException {
        BufferedReader fin = new BufferedReader(new FileReader(filename));
        return new LinesInput(fin);
    }
}