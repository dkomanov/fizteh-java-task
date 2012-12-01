package ru.fizteh.fivt.students.tolyapro.parallelSort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.LinkedBlockingQueue;

public class Sorter implements Runnable {

    ArrayList<String> strings;
    LinkedBlockingQueue<ArrayList<String>> queue;
    Comparator<String> comparator;
    int currPosition;
    boolean taken;

    Sorter(ArrayList<String> strings, Comparator<String> comparator,
            LinkedBlockingQueue<ArrayList<String>> queue) {
        this.strings = strings;
        this.queue = queue;
        this.comparator = comparator;
        currPosition = 0;
    }

    @Override
    public void run() {
        Collections.sort(strings, comparator);
        // queue.add(strings);
    }

    boolean hasNext() {
        return currPosition != strings.size();
    }

    void next() {
        currPosition++;
    }

    String get() {
        return strings.get(currPosition);
    }

    void take() {
        taken = true;
    }

    boolean isTaken() {
        return taken;
    }

    void release() {
        taken = false;
    }
}
