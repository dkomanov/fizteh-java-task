package ru.fizteh.fivt.students.altimin.sorter;

import java.util.*;

/**
 * User: altimin
 * Date: 11/21/12
 * Time: 5:51 AM
 */
public class ParallelSorter<T> {
    private final int OPTIMAL_ARRAY_LENGTH_PER_THREAD = 100000;
    private Comparator<T> comparator;
    private Class elementType;

    public ParallelSorter(Comparator<T> comparator, Class elementType) {
        this.comparator = comparator;
        this.elementType = elementType;
    }

    private T[] merge(List<T[]> arrays) {
        List<T> result = new LinkedList<T>();
        class ArrayWrapper {
            public T[] array;
            public int offset;
            int id;

            ArrayWrapper(T[] array, int offset, int id) {
                this.array = array;
                this.offset = offset;
                this.id = id;
            }

            ArrayWrapper(T[] array, int id) {
                this.array = array;
                this.offset = 0;
                this.id = id;
            }
        }
        class ArrayComparator implements Comparator<ArrayWrapper> {
            Comparator<T> comparator;

            ArrayComparator(Comparator<T> comparator) {
                this.comparator = comparator;
            }

            @Override
            public int compare(ArrayWrapper lhs, ArrayWrapper rhs) {
                int comparisonResult = comparator.compare(lhs.array[lhs.offset], rhs.array[rhs.offset]);
                if (comparisonResult == 0) {
                    return lhs.id - rhs.id;
                } else {
                    return comparisonResult;
                }
            }
        }
        TreeSet<ArrayWrapper> set = new TreeSet<ArrayWrapper>(new ArrayComparator(comparator));
        int id = 0;
        for (T[] array: arrays) {
            if (array.length > 0) {
                set.add(new ArrayWrapper(array, id ++));
            }
        }
        while (!set.isEmpty()) {
            ArrayWrapper arrayWrapper = set.pollFirst();
            result.add(arrayWrapper.array[arrayWrapper.offset]);
            arrayWrapper.offset += 1;
            if (arrayWrapper.offset < arrayWrapper.array.length) {
                set.add(arrayWrapper);
            }
        }
        return result.toArray((T[])java.lang.reflect.Array.newInstance(elementType, 0));
    }

    public T[] sort(T[] array, int threadsCount) {
        threadsCount = Math.min(threadsCount, array.length / OPTIMAL_ARRAY_LENGTH_PER_THREAD + 1);
        List<T[]> sortedSubArrays = new ArrayList<T[]>();
        int position = 0;
        int averageSubArrayLength = array.length / threadsCount;
        List<Thread> threadList = new ArrayList<Thread>();
        for (int i = 0; i < threadsCount; i ++) {
            int length = (i + 1 == threadsCount) ? array.length - position : averageSubArrayLength;
            T[] subArray = Arrays.copyOfRange(array, position, position + length);
            Thread thread = new Thread(new ArraySorter<T>(subArray, comparator));
            thread.start();
            threadList.add(thread);
            sortedSubArrays.add(subArray);
            position += length;
        }
        for (Thread thread: threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
        return merge(sortedSubArrays);
    }
}
