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

    List<String> string;
    List<Integer> count;
    Boolean ignoreCase;

    private int compare(String x,String y) {
        if (ignoreCase) {
            return x.compareToIgnoreCase(y);
        } else {
            return x.compareTo(y);
        }
    }

    public ResultContainer(boolean ignoreCase, List<String> data) {
        int currSize = 0;
        this.ignoreCase = ignoreCase;
        string  = new ArrayList<String>();
        count = new ArrayList<Integer>();
        for (String s: data) {
            if (currSize != 0  &&  compare(string.get(currSize - 1), s) == 0) {
                count.set(currSize - 1, count.get(currSize - 1) + 1);
            } else {
                string.add(s);
                count.add(1);
                currSize++;
            }
        }
        //System.out.println("OK");
        //System.out.flush();
    }

    public Integer size() {
        return string.size();
    }

    public void add(ResultContainer data) {
        List<String> newString = new ArrayList<String>();
        List<Integer> newCount = new ArrayList<Integer>();
        int i = 0, j = 0;
        while (i < this.size()  ||  j < data.size()) {
            if (i == this.size()) {
                newString.add(data.string.get(j));
                newCount.add(data.count.get(j));
                j++;
            } else if (j == data.size()) {
                newString.add(string.get(i));
                newCount.add(count.get(i));
                i++;
            } else {
                int compResult = compare(string.get(i), data.string.get(j));
                if (compResult == 0) {
                    newString.add(data.string.get(j));
                    newCount.add(data.count.get(j) + count.get(i));
                    i++;
                    j++;
                } else if (compResult > 0) {
                    newString.add(data.string.get(j));
                    newCount.add(data.count.get(j));
                    j++;
                } else {
                    newString.add(string.get(i));
                    newCount.add(count.get(i));
                    i++;
                }
            }
        }
        string = newString;
        count = newCount;
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
                for (String s: string) {
                    writer.write(s + '\n');
                    writer.flush();
                }
            } else {
                for (int i = 0; i < size(); i++) {
                    String s = string.get(i);
                    for (int j = 0; j < count.get(i); j++) {
                        writer.write(s + '\n');
                        writer.flush();
                    }
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } finally {
            IOUtils.tryClose(fWriter);
            IOUtils.tryClose(writer);
            IOUtils.tryClose(oStreamWriter);
        }
    }

}
