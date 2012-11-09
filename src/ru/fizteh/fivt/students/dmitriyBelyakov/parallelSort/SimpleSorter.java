package ru.fizteh.fivt.students.dmitriyBelyakov.parallelSort;

import java.util.Comparator;
import java.util.Collections;
import java.util.List;

class ComparatorIgnoreCase implements Comparator<String> {
    public int compare(String s1, String s2) {
        return s1.compareToIgnoreCase(s2);
    }
}

public class SimpleSorter implements Runnable {
    private List<String> valuesForSorting;
    private int curValue;
    private Thread thread;
    private boolean ignoreCase;

    SimpleSorter(List<String> values, boolean ignoreCase) {
        valuesForSorting = values;
        curValue = 0;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public void run() {
        if (ignoreCase) {
            Collections.sort(valuesForSorting, new ComparatorIgnoreCase());
        } else {
            Collections.sort(valuesForSorting);
        }
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    public void join() throws InterruptedException {
        thread.join();
    }

    boolean hasValue() {
        return (curValue < valuesForSorting.size());
    }

    String currentValue() {
        return valuesForSorting.get(curValue);
    }

    void nextValue() {
        if (hasValue()) {
            ++curValue;
        }
    }
}
