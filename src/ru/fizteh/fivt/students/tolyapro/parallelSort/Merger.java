package ru.fizteh.fivt.students.tolyapro.ParallelSort;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Merger implements Runnable {
    LinkedBlockingQueue<ArrayList<String>> queue;
    boolean caseSensitive;

    public Merger(boolean sensitive,
            LinkedBlockingQueue<ArrayList<String>> queue) {
        this.queue = queue;
        caseSensitive = sensitive;
    }

    @Override
    public void run() {
        boolean doneNothing = true;
        while (doneNothing) {
            ArrayList<String> firstList = null;
            ArrayList<String> secondList = null;
            synchronized (queue) {
                if (queue.size() > 1) {
                    firstList = queue.poll();
                    secondList = queue.poll();
                }
            }
            if (firstList != null && secondList != null) {
                doneNothing = false;
                ArrayList<String> resultList = new ArrayList<String>();
                int i = 0, j = 0;
                int m = firstList.size();
                int n = secondList.size();
                if (caseSensitive) {
                    while (i < m && j < n) {
                        resultList.add(firstList.get(i).compareTo(
                                secondList.get(j)) <= 0 ? firstList.get(i++)
                                : secondList.get(j++));
                    }
                } else {
                    while (i < m && j < n) {
                        resultList.add(firstList.get(i).compareToIgnoreCase(
                                secondList.get(j)) <= 0 ? firstList.get(i++)
                                : secondList.get(j++));
                    }
                }
                while (i < m) {
                    resultList.add(firstList.get(i++));
                }
                while (j < n) {
                    resultList.add(secondList.get(j++));
                }
                queue.add(resultList);
            }

        }

    }

}
