package ru.fizteh.fivt.students.nikitaAntonov.parallelsort;

import java.util.ArrayList;
import java.util.Collections;

class SortingTask implements Runnable {

    private ArrayList<Line> chunk;
    private ParallelSorter sorter;

    public SortingTask(ArrayList<Line> toSort, ParallelSorter s) {
        chunk = toSort;
        sorter = s;
    }

    @Override
    public void run() {
        Collections.sort(chunk, sorter.cmp);
        sorter.mergeQueue.add(chunk);
    }
}
