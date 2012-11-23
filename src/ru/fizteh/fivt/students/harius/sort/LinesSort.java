package ru.fizteh.fivt.students.harius.sort;

import java.util.List;
import java.util.Queue;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.Comparator;

public class LinesSort implements Runnable {
    private List<String> data;
    private Queue<List<String>> q;
    private Comparator<String> comp;

    public LinesSort(List<String> data, Queue<List<String>> q, Comparator<String> comp) {
        this.data = data;
        this.q = q;
        this.comp = comp;
    }

    @Override
    public void run() {
        Collections.sort(data, comp);
        q.add(data);
    }
}