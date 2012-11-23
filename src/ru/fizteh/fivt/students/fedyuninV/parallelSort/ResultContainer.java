package ru.fizteh.fivt.students.fedyuninV.parallelSort;

import ru.fizteh.fivt.students.fedyuninV.IOUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.*;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class ResultContainer {

    List<StringContainer> strings;
    Comparator<StringContainer> comparator;
    boolean ignoreCase;

    public ResultContainer(boolean ignoreCase, List<StringContainer> data) {
        this.ignoreCase = ignoreCase;
        if (ignoreCase) {
            this.comparator = new StringContainer.CaseInsensitiveComparator();
        } else {
            this.comparator = new StringContainer.DefaultComparator();
        }
        strings  = data;
    }

    public Comparator<StringContainer> getComparator() {
        return comparator;
    }

    private int compareStrings(String x, String y) {
        if (ignoreCase) {
            return x.compareToIgnoreCase(y);
        } else {
            return x.compareTo(y);
        }
    }

    public Integer size() {
        return strings.size();
    }

    public void add(ResultContainer data) {
        List<StringContainer> newStrings = new ArrayList<StringContainer>();
        int i = 0, j = 0;
        while (i < this.size()  ||  j < data.size()) {
            if (i == this.size()) {
                newStrings.add(data.strings.get(j));
                j++;
            } else if (j == data.size()) {
                newStrings.add(strings.get(i));
                i++;
            } else {
                if (comparator.compare(strings.get(i), data.strings.get(j)) < 0) {
                    newStrings.add(strings.get(i));
                    i++;
                } else {
                    newStrings.add(data.strings.get(j));
                    j++;
                }
            }
        }
        strings = newStrings;
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
            for (int i = 0; i < strings.size(); i++) {
                if (!unique  ||  (i == 0  ||  compareStrings(strings.get(i - 1).string(), strings.get(i).string()) < 0)) {
                    writer.write(strings.get(i).string() + '\n');
                    writer.flush();
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } finally {
            IOUtils.tryClose(fWriter);
        }
    }

}
