package ru.fizteh.fivt.students.verytable.sort;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.List;

public class SortTask implements Runnable {
    private int SPLIT_THRESHOLD = 1024 * 1024;
    private String[] stringsToSort;
    private int startIndex = 0;
    private int endIndex  = 0;
    private boolean isSensitiveKey;
    ExecutorService threadPool;
    List<Future> futureList;

    public SortTask(ExecutorService threadPool, List<Future> futureList,
                    String[] stringsToSort, int start, int end,
        boolean isSensitiveKey){
        this.stringsToSort = stringsToSort;
        this.startIndex = start;
        this.endIndex = end;
        this.isSensitiveKey = isSensitiveKey;
        this.threadPool = threadPool;
        this.futureList = futureList;
    }

    public void run() {
        sort(stringsToSort, startIndex, endIndex) ;
    }

    private void sort(final String[] stringsToSort, int start, int end) {
        String pivot = stringsToSort[start];
        int leftPointer = start;
        int rightPointer = end;
        final int LEFT = 1;
        final int RIGHT = -1;
        int pointerSide = RIGHT;

        while (leftPointer != rightPointer) {
            if (pointerSide == RIGHT) {
                if (isSensitiveKey) {
                    if (stringsToSort[rightPointer].compareTo(pivot) < 0) {
                        stringsToSort[leftPointer] = stringsToSort[rightPointer];
                        ++leftPointer;
                        pointerSide = LEFT;
                    } else {
                        --rightPointer;
                    }
                } else {
                    if (stringsToSort[rightPointer].compareToIgnoreCase(pivot) < 0) {
                        stringsToSort[leftPointer] = stringsToSort[rightPointer];
                        ++leftPointer;
                        pointerSide = LEFT;
                    } else {
                        --rightPointer;
                    }
                }
            } else {
                if (isSensitiveKey) {
                    if (stringsToSort[leftPointer].compareTo(pivot) > 0) {
                        stringsToSort[rightPointer] = stringsToSort[leftPointer];
                        --rightPointer;
                        pointerSide = RIGHT;
                    } else {
                        ++leftPointer;
                    }
                } else {
                    if (stringsToSort[leftPointer].compareToIgnoreCase(pivot) > 0) {
                        stringsToSort[rightPointer]= stringsToSort[leftPointer];
                        --rightPointer;
                        pointerSide = RIGHT;
                    } else {
                        ++leftPointer;
                    }
                }
            }
        }

        stringsToSort[leftPointer] = pivot;

        if((leftPointer - start) > 1){
            if ((leftPointer - start) > SPLIT_THRESHOLD){
                futureList.add(threadPool.submit(new SortTask(threadPool, futureList, stringsToSort,
                                                 start, leftPointer - 1, isSensitiveKey)));
            }else {
                if (!isSensitiveKey) {
                    Arrays.sort(stringsToSort, start, leftPointer, String.CASE_INSENSITIVE_ORDER);
                } else {
                    Arrays.sort(stringsToSort, start, leftPointer);
                }
            }
        }

        if((end - leftPointer) > 1){
            if ((end - leftPointer) > SPLIT_THRESHOLD ){
                futureList.add(threadPool.submit(new SortTask(threadPool, futureList, stringsToSort,
                                                 leftPointer + 1, end, isSensitiveKey)));
            }  else {
                if (!isSensitiveKey) {
                    Arrays.sort(stringsToSort, leftPointer + 1, end + 1, String.CASE_INSENSITIVE_ORDER);
                } else {
                    Arrays.sort(stringsToSort, leftPointer + 1, end + 1);
                }
            }
        }

    }

}