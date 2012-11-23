package ru.fizteh.fivt.students.harius.sort;

import java.util.List;
import java.util.Queue;
import java.util.Collections;
import java.util.concurrent.Callable;

public class LinesSort implements Runnable {
    private List<String> data;
    private Queue<List<String>> q;

    public LinesSort(List<String> data, Queue<List<String>> q) {
        this.data = data;
        this.q = q;
    }

    @Override
    public void run() {
        Collections.sort(data);
        q.add(data);
    }
}