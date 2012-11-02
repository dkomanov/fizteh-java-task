package ru.fizteh.fivt.students.fedyuninV.parallelSort;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class ResultContainer {

    private SortedMap<String, Integer> container = null;


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
            //System.out.println(entry.getKey());
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
            try {
                if (fWriter != null) {
                    fWriter.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (oStreamWriter != null) {
                    oStreamWriter.close();
                }
            } catch (Exception exc) {
                System.err.println(exc.getMessage());
                System.exit(1);
            }
        }
    }

}
