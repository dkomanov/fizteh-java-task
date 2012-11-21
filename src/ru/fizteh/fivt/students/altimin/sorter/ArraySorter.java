package ru.fizteh.fivt.students.altimin.sorter;

import java.util.Arrays;
import java.util.Comparator;

/**
 * User: altimin
 * Date: 11/21/12
 * Time: 5:46 AM
 */
public class ArraySorter<T> implements Runnable {
    private T[] array;
    private Comparator<T> comparator;

    public ArraySorter(T[] array) {
        this.array = array;
    }

    public ArraySorter(T[] array, Comparator<T> comparator) {
        this.array = array;
        this.comparator = comparator;
    }

    @Override
    public void run() {
        if (comparator == null) {
            Arrays.sort(array);
        } else {
            Arrays.sort(array, comparator);
        }
    }
}
