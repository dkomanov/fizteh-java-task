package ru.fizteh.fivt.students.mysinYurii.parallelSort;

import java.util.List;
import java.util.Collections;
import java.util.concurrent.LinkedBlockingQueue;

public class MyThread implements Runnable {
    LinkedBlockingQueue<String> stringQueue;
    
    List<String> resultList;
    
    boolean caseSensitive;
    
    public MyThread(LinkedBlockingQueue<String> inputStrings, List<String> toMerge,
            boolean caseSense) {
        stringQueue = inputStrings;
        resultList = toMerge;
        caseSensitive = caseSense;
    }

    @Override
    public void run() {
        String newString = null;
        do {
            try {
                newString = stringQueue.take();
            } catch (InterruptedException e) {
                System.exit(1);
            }
            if (!newString.equals("")) {
                resultList.add(newString);
            } else {
                break;
            }
        } while (true);
        if (caseSensitive) {
            Collections.sort(resultList);
        } else {
            Collections.sort(resultList, String.CASE_INSENSITIVE_ORDER);
        }
    }
}
