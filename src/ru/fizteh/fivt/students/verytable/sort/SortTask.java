package ru.fizteh.fivt.students.verytable.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class SortTask implements Runnable {
    private int SPLIT_THRESHOLD = 1024 * 1024;
    private ArrayList<Pair> stringsToSort;
    private int startIndex = 0;
    private int endIndex = 0;
    private boolean isSensitiveKey;
    private Comparator<Pair> comparator;
    ExecutorService threadPool;
    List<Future> futureList;

    public SortTask(ExecutorService threadPool, List<Future> futureList,
                    ArrayList<Pair> stringsToSort, int start, int end,
                    boolean isSensitiveKey) {
        this.stringsToSort = stringsToSort;
        this.startIndex = start;
        this.endIndex = end;
        this.isSensitiveKey = isSensitiveKey;
        this.threadPool = threadPool;
        this.futureList = futureList;
        this.comparator = new SensitivePairComparator();
        if (!isSensitiveKey) {
            this.comparator = new InsensitivePairComparator();
        }
    }

    public void run() {
        sort(startIndex, endIndex);
    }

    static class SensitivePairComparator implements Comparator<Pair> {

        public int compare(Pair pair1, Pair pair2) {
            int ans = pair1.getValue().compareTo(pair2.getValue());
            if (ans == 0) {
                return (pair1.id - pair2.id);
            } else {
                return ans;
            }
        }
    }

    static class InsensitivePairComparator implements Comparator<Pair> {

        public int compare(Pair pair1, Pair pair2) {
            int ans = pair1.getValue().compareToIgnoreCase(pair2.getValue());
            if (ans == 0) {
                return (pair1.id - pair2.id);
            } else {
                return ans;
            }
        }
    }

    private void sort(int start, int end) {
        Pair pivot = stringsToSort.get(start);
        int leftPointer = start;
        int rightPointer = end;
        final int LEFT = 1;
        final int RIGHT = -1;
        int pointerSide = RIGHT;

        while (leftPointer != rightPointer) {
            if (pointerSide == RIGHT) {
                if (comparator.compare(stringsToSort.get(rightPointer), pivot) < 0) {
                    stringsToSort.set(leftPointer, stringsToSort.get(rightPointer));
                    ++leftPointer;
                    pointerSide = LEFT;
                } else {
                    --rightPointer;
                }
            } else {
                if (comparator.compare(stringsToSort.get(leftPointer), pivot) > 0) {
                    stringsToSort.set(rightPointer, stringsToSort.get(leftPointer));
                    --rightPointer;
                    pointerSide = RIGHT;
                } else {
                    ++leftPointer;
                }
            }
        }

        stringsToSort.set(leftPointer, pivot);

        if ((leftPointer - start) > 1) {
            if ((leftPointer - start) > SPLIT_THRESHOLD) {
                futureList.add(threadPool.submit(new SortTask(threadPool, futureList, stringsToSort,
                               start, leftPointer - 1, isSensitiveKey)));
            } else {
                Collections.sort(stringsToSort.subList(start, leftPointer), comparator);
            }
        }

        if ((end - leftPointer) > 1) {
            if ((end - leftPointer) > SPLIT_THRESHOLD) {
                futureList.add(threadPool.submit(new SortTask(threadPool, futureList, stringsToSort,
                               leftPointer + 1, end, isSensitiveKey)));
            } else {
                Collections.sort(stringsToSort.subList(leftPointer + 1, end + 1), comparator);
            }
        }

    }

}
