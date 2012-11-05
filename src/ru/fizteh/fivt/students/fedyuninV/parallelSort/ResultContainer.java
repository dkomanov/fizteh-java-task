package ru.fizteh.fivt.students.fedyuninV.parallelSort;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class ResultContainer {

    private SortedMap<String, Integer> container = null;

    private static <T extends Closeable> void tryClose(T stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
    }

    public ResultContainer(boolean ignoreCase) {
        if (ignoreCase) {
            container = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
        } else {
            container = new TreeMap<String, Integer>();
        }
    }

    public void add(ResultContainer given) {
        if (given == null) {
            return;
        }
        for (SortedMap.Entry<String, Integer> entry: given.container.entrySet()) {
            Integer currCount = container.get(entry.getKey());
            if (currCount != null) {
                container.put(entry.getKey(), currCount + entry.getValue());
            } else {
                container.put(entry.getKey(), entry.getValue());
            }
        }

    }

    public void add(String word) {
        Integer currCount = container.get(word);
        if (currCount != null) {
            container.put(word, currCount + 1);
        } else {
            container.put(word, 1);
        }
    }

    public void print(boolean unique, String fileName) {
        FileWriter fWriter = null;
        BufferedWriter writer = null;
        OutputStreamWriter oStreamWriter = null;
        try {
            if (fileName == null) {
                oStreamWriter = new OutputStreamWriter(System.out);
                writer = new BufferedWriter(oStreamWriter);
            } else {
                fWriter = new FileWriter(fileName);
                writer = new BufferedWriter(fWriter);
            }
            if (unique) {
                for (Map.Entry<String, Integer> entry: container.entrySet()) {
                    writer.write(entry.getKey() + '\n');
                    writer.flush();
                }
            } else {
                for (Map.Entry<String, Integer> entry: container.entrySet()) {
                    for (int i = 0; i < entry.getValue(); i++) {
                        writer.write(entry.getKey() + '\n');
                        writer.flush();
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } finally {
            tryClose(fWriter);
            tryClose(writer);
            tryClose(oStreamWriter);
        }
    }

}
