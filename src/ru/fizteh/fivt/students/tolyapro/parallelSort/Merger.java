package ru.fizteh.fivt.students.tolyapro.parallelSort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.LinkedBlockingQueue;

public class Merger implements Runnable {
    LinkedBlockingQueue<ArrayList<String>> queue;
    Comparator<String> comparator;
    int number;

    public Merger(Comparator<String> comparator,
            LinkedBlockingQueue<ArrayList<String>> queue, int number) {
        this.queue = queue;
        this.comparator = comparator;
        this.number = number;
    }

    @Override
    public void run() {
        boolean doneNothing = true;
        while (doneNothing) {
            //System.out.println(number + " : " + queue.size());
            ArrayList<String> firstList = null;
            ArrayList<String> secondList = null;
            synchronized (queue) {
                if (queue.size() == number) {
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
               
                //synchronized (queue) {
                 //   System.out.println(number + " l:l " + queue.size());
                    queue.add(resultList);
               // }
            }

        }

    }

}
