package ru.fizteh.fivt.students.yushkevichAnton.parallelsort;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SlaveSorter extends Thread {
    final ArrayList<String> strings;

    final Comparator<String> comparator;

    final AtomicInteger threadsLeft;

    final int l, r;

    public SlaveSorter(ArrayList<String> strings, Comparator<String> comparator, AtomicInteger threadCount, int l, int r) {
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
            Thread slave = new SlaveSorter(strings, comparator, threadsLeft, l, m);
            slave.start();
            sort(m, r);
            try {
                slave.join();
            } catch (InterruptedException e) {
                System.err.println("Sorting interrupted");
                System.exit(1);
            }
        } else {
            sort(l, m);
            sort(m, r);
        }

        int i = l, j = m;

        ArrayList<String> sorted = new ArrayList<String>();
        while (i < m || j < r) {
            if (j == r || (i < m && comparator.compare(strings.get(i), strings.get(j)) <= 0)) {
                sorted.add(strings.get(i++));
            } else {
                sorted.add(strings.get(j++));
            }
        }
        for (i = 0; i < r - l; i++) {
            strings.set(l + i, sorted.get(i));
        }
    }
}