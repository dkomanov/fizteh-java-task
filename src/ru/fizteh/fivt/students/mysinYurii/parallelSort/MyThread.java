package ru.fizteh.fivt.students.mysinYurii.parallelSort;

import java.util.List;
import java.util.Collections;

public class MyThread implements Runnable {
    List<String> strings;
    
    List<String> resultList;
    
    boolean caseSensitive;
    
    int currentPosition;
    
    int sizeOfData;
    
    public MyThread(List<String> inputStrings, List<String> toMerge,
            boolean caseSense, int currPos, int sortSize) {
        strings = inputStrings;
        resultList = toMerge;
        caseSensitive = caseSense;
        currentPosition = currPos;
        sizeOfData = sortSize;
    }

    @Override
    public void run() {
        for (int i = currentPosition; i < currentPosition + sizeOfData; ++i) {
            resultList.add(strings.get(i));
        }
        if (caseSensitive) {
            Collections.sort(resultList);
        } else {
            Collections.sort(resultList, String.CASE_INSENSITIVE_ORDER);
        }
    }
}
