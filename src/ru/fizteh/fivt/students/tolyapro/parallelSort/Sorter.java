package ru.fizteh.fivt.students.tolyapro.ParallelSort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.LinkedBlockingQueue;

public class Sorter implements Runnable {

    ArrayList<String> strings;
    LinkedBlockingQueue<ArrayList<String>> queue;
    Comparator<String> comparator;

    Sorter(ArrayList<String> strings, Comparator<String> comparator,
            LinkedBlockingQueue<ArrayList<String>> queue) {
        this.strings = strings;
        this.queue = queue;
        this.comparator = comparator;
    }

    @Override
    public void run() {
        Collections.sort(strings, comparator);
        queue.add(strings);
    }
}
