package ru.fizteh.fivt.students.tolyapro.parallelSort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.LinkedBlockingQueue;

public class Merger implements Runnable {
    LinkedBlockingQueue<ArrayList<String>> queue;
    Comparator<String> comparator;

    public Merger(Comparator<String> comparator,
            LinkedBlockingQueue<ArrayList<String>> queue) {
        this.queue = queue;
        this.comparator = comparator;
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
                while (i < m && j < n) {
                    resultList.add(comparator.compare(firstList.get(i),
                            (secondList.get(j))) <= 0 ? firstList.get(i++)
                            : secondList.get(j++));
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
