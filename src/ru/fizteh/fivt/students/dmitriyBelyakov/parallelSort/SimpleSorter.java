package ru.fizteh.fivt.students.dmitriyBelyakov.parallelSort;

import java.util.Collections;
import java.util.List;

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
            Collections.sort(valuesForSorting, String.CASE_INSENSITIVE_ORDER);
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
