package ru.fizteh.fivt.students.nikitaAntonov.parallelsort;

import java.util.ArrayList;
import java.util.Collections;

class SortingTask implements Runnable {

    private ArrayList<String> chunk;
    private ParallelSorter sorter;

    public SortingTask(ArrayList<String> toSort, ParallelSorter s) {
        chunk = toSort;
        sorter = s;
    }

    @Override
    public void run() {
        Collections.sort(chunk, sorter.cmp);
        sorter.mergeQueue.add(chunk);
    }
}
