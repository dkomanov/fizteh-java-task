package ru.fizteh.fivt.students.alexanderKuzmin.parallelSort;

import java.util.ArrayList;
import java.util.Collections;
import ru.fizteh.fivt.students.alexanderKuzmin.Closers;

/**
 * @author Alexander Kuzmin group 196 Class ParallelMergeSort
 * 
 */

public class ParallelMergeSort implements Runnable {
    private int limit;
    private ArrayList<String> strings;
    int numberOfThreads;
    final int startPos, endPos;
    ArrayList<String> result;
    boolean insensitive;

    ParallelMergeSort(ArrayList<String> otherStrings, int otherStartPos,
            int otherEndPos, int otherNumberOfThreads, int otherLimit,
            boolean otherInsensitive) {
        strings = otherStrings;
        startPos = otherStartPos;
        endPos = otherEndPos;
        numberOfThreads = otherNumberOfThreads;
        result = new ArrayList<String>();
        limit = otherLimit;
        insensitive = otherInsensitive;
    }

    private void merge(ParallelMergeSort left, ParallelMergeSort right) {
        int leftPos = 0;
        int rightPos = 0;
        int leftSize = left.size();
        int rightSize = right.size();
        while (leftPos < leftSize && rightPos < rightSize) {
            try {
                if (insensitive) {
                    result.add((left.result.get(leftPos).compareToIgnoreCase(
                            right.result.get(rightPos)) <= 0) ? left.result
                            .get(leftPos++) : right.result.get(rightPos++));
                } else {
                    result.add((left.result.get(leftPos).compareTo(
                            right.result.get(rightPos)) <= 0) ? left.result
                            .get(leftPos++) : right.result.get(rightPos++));
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        while (leftPos < leftSize) {
            result.add(left.result.get(leftPos++));
        }
        while (rightPos < rightSize) {
            result.add(right.result.get(rightPos++));
        }
    }

    public int size() {
        return endPos - startPos;
    }

    @Override
    public void run() {
        this.compute();
    }

    private void compute() {
        if (size() <= limit) {
            for (int i = startPos; i < endPos; ++i) {
                result.add(strings.get(i));
            }
            if (insensitive) {
                Collections.sort(result, String.CASE_INSENSITIVE_ORDER);
            } else {
                Collections.sort(result);
            }
        } else {
            int midpoint = size() / 2;
            ParallelMergeSort left = new ParallelMergeSort(strings, startPos,
                    startPos + midpoint, --numberOfThreads, limit, insensitive);
            Thread thread = null;
            if (numberOfThreads > 0) {
                thread = new Thread(left);
                thread.start();
            } else {
                left.compute();
            }
            ParallelMergeSort right = new ParallelMergeSort(strings, startPos
                    + midpoint, endPos, numberOfThreads, limit, insensitive);
            right.compute();
            if (numberOfThreads > 0) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Closers.printErrAndExit(e.getMessage());
                }
            }
            merge(left, right);
        }
    }
}
