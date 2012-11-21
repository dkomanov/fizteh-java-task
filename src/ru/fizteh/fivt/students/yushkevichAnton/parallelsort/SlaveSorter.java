package ru.fizteh.fivt.students.yushkevichAnton.parallelsort;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SlaveSorter extends Thread {
    private String[] strings;

    private Comparator<String> comparator;

    private final AtomicInteger threadsLeft;

    private int l, r;

    public SlaveSorter(String[] strings, Comparator<String> comparator, AtomicInteger threadCount, int l, int r) {
        this.strings = strings;
        this.comparator = comparator;
        this.threadsLeft = threadCount;
        this.l = l;
        this.r = r;
    }

    @Override
    public void run() {
        sort(l, r);
    }

    private void sort(int l, int r) {
        if (r - l < 2) {
            return;
        }

        int m = (l + r) / 2;

        boolean parallelMode = false;
        synchronized (threadsLeft) {
            if (threadsLeft.get() > 0) {
                parallelMode = true;
                threadsLeft.decrementAndGet();
            }
        }
        if (parallelMode) {
            Thread assistant = new SlaveSorter(strings, comparator, threadsLeft, l, m);
            assistant.start();
            sort(m, r);
            try {
                assistant.join();
            } catch (InterruptedException e) {
                System.err.println("Sorting interrupted");
                System.exit(1);
            }
        } else {
            sort(l, m);
            sort(m, r);
        }

        int i = l, j = m;

        String[] sorted = new String[r - l];
        int c = 0;
        while (i < m || j < r) {
            if (j == r || (i < m && comparator.compare(strings[i], strings[j]) <= 0)) {
                sorted[c++] = strings[i++];
            } else {
                sorted[c++] = strings[j++];
            }
        }
        for (i = 0; i < c; i++) {
            strings[l + i] = sorted[i];
        }
    }
}