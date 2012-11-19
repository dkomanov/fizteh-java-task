package ru.fizteh.fivt.students.yushkevichAnton.parallelsort;

import java.util.*;
import java.util.concurrent.Executors;

public class SlaveSorter extends Thread {
    private ArrayList<String>  strings;
    private Comparator<String> comparator;

    private int l, r;

    private SlaveSorter() {}

    public SlaveSorter(ArrayList<String> strings, Comparator<String> comparator, int l, int r) {
        this.strings = strings;
        this.comparator = comparator;
        this.l = l;
        this.r = Math.min(r, strings.size());
    }

    @Override
    public void run() {
        int m = (l + r) / 2;

        int i = l, j = m;

        ArrayList<String> sorted = new ArrayList<String>();
        while (i < m || j < r) {
            if (j == r || (i < m && comparator.compare(strings.get(i), strings.get(j)) <= 0)) {
                sorted.add(strings.get(i++));
            } else {
                sorted.add(strings.get(j++));
            }
        }
        for (i = 0; i < sorted.size(); i++) {
            strings.set(l + i, sorted.get(i));
        }

    }
}