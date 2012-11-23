package ru.fizteh.fivt.students.tolyapro.parallelSort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.LinkedBlockingQueue;

public class Sorter implements Runnable {

    ArrayList<String> strings;
    LinkedBlockingQueue<ArrayList<String>> queue;
    Comparator<String> comparator;
    int number;

    Sorter(ArrayList<String> strings, Comparator<String> comparator,
            LinkedBlockingQueue<ArrayList<String>> queue, int number) {
        this.strings = strings;
        this.queue = queue;
        this.comparator = comparator;
        this.number = number;
    }

    @Override
    public void run() {
        Collections.sort(strings, comparator);

        while (queue.size() != number) {
            ;
        }
        synchronized (queue) {
            queue.add(strings);
        }
    }
}
