package ru.fizteh.fivt.students.yuliaNikonova.parallelSort;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Merger extends Thread {
    private LinkedBlockingQueue<String> list1;
    private LinkedBlockingQueue<String> list2;
    private LinkedBlockingQueue<String> resultList;
    private StringComparator stringComp;
    private int num;

    public Merger(LinkedBlockingQueue<String> List1, LinkedBlockingQueue<String> List2, StringComparator stringComp) {
        this.list1 = List1;
        this.list2 = List2;
        this.stringComp = stringComp;
        this.resultList = new LinkedBlockingQueue<String>();
        //this.num = n;
    }

    public void run() {
        // System.out.println("Size of first is " + list1.size());
        // System.out.println("Size of second is " + list2.size());
        while (!list1.isEmpty() && !list2.isEmpty()) {
            // System.out.println(num + "   " + list1.size() + "  " +
            // list2.size());
            // System.out.println("Size of second is " + list2.size());
            String val1 = list1.element();
            String val2 = list2.element();
            if (stringComp.compare(val1, val2)<=0) {
                
                    resultList.add(val1);
                    list1.poll();
                } else {
                    resultList.add(val2);
                    list2.poll();
                }
            
                
         
        }

        // System.out.println("Something empty: " + list1.size() + " " +
        // list2.size());
        if (!list1.isEmpty()) {
            resultList.addAll(list1);
            // System.out.println("I added list1");
        }
        if (!list2.isEmpty()) {
            resultList.addAll(list2);
        }

        // System.out.println("I've done");
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
