package ru.fizteh.fivt.students.dmitriyBelyakov.parallelSort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SimpleSorter implements Runnable {
    private List<String> valuesForSorting;
    private int curValue;
    private Thread thread;

    SimpleSorter(List<String> values) {
        valuesForSorting = values;
        curValue = 0;
    }

    @Override
    public void run() {
        Collections.sort(valuesForSorting);
        System.out.println(valuesForSorting);
        try {
            thread.sleep(1000);
        } catch(Exception e) {}
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
        if(hasValue()) {
            ++curValue;
        }
    }
}