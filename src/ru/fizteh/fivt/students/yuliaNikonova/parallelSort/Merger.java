package ru.fizteh.fivt.students.yuliaNikonova.parallelSort;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Merger extends Thread {
    private LinkedBlockingQueue<String> list1;
    private LinkedBlockingQueue<String> list2;
    private LinkedBlockingQueue<String> resultList;
    private Comparator<String> stringComp;

    public Merger(LinkedBlockingQueue<String> List1, LinkedBlockingQueue<String> List2, Comparator<String> stringComp) {
        this.list1 = List1;
        this.list2 = List2;
        this.stringComp = stringComp;
        this.resultList = new LinkedBlockingQueue<String>();
    }

    public void run() {
        int size1 = list1.size();
        int size2 = list2.size();
        int cur1 = 0;
        int cur2 = 0;
        while (cur1 < size1 && cur2 < size2) {
            String val1 = list1.element();
            String val2 = list2.element();
            if (stringComp.compare(val1, val2) <= 0) {
                resultList.add(val1);
                list1.poll();
                cur1++;
            } else {
                resultList.add(val2);
                list2.poll();
                cur2++;
            }
        }
        if (cur1 != size1) {
            resultList.addAll(list1);
        }
        if (cur2 != size2) {
            resultList.addAll(list2);
        }

    }

    LinkedBlockingQueue<String> getResult() {
        return resultList;
    }

    /* public void showResults() {
     * System.out.println("==============");
     * for (String strLine : resultList) {
     * System.out.println(strLine);
     * }
     * 
     * System.out.println("==============");
     * } */
}