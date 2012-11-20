package ru.fizteh.fivt.students.tolyapro.ParallelSort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;

public class Sorter implements Runnable {

    boolean caseSensitive;
    ArrayList<String> strings;
    LinkedBlockingQueue<ArrayList<String>> queue;

    Sorter(ArrayList<String> strings, boolean sensitive,
            LinkedBlockingQueue<ArrayList<String>> queue) {
        this.strings = strings;
        this.queue = queue;
        caseSensitive = sensitive;
        System.out.println("");
    }

    @Override
    public void run() {
        System.out.println("Started sorting");
        // synchronized (queue) {
        if (caseSensitive) {
            Collections.sort(strings);
            queue.add(strings);
        } else {
            Collections.sort(strings, String.CASE_INSENSITIVE_ORDER);
            queue.add(strings);
        }
        System.out.println("Finished sorting");
        // }
    }
}
