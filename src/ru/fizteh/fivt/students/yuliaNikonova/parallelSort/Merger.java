package ru.fizteh.fivt.students.yuliaNikonova.parallelSort;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

public class Merger extends Thread {
    private CopyOnWriteArrayList<String> List1;
    private CopyOnWriteArrayList<String> List2;
    private CopyOnWriteArrayList<String> ResultList;
    private boolean ignoreCase;

    public Merger(CopyOnWriteArrayList<String> List1, CopyOnWriteArrayList<String> List2, boolean ignoreCase) {
        this.List1 = List1;
        this.List2 = List2;
        this.ignoreCase = ignoreCase;
        this.ResultList = new CopyOnWriteArrayList<String>();
    }

    public void run() {
        while (!List1.isEmpty() && !List2.isEmpty()) {
            String val1 = List1.get(0);
            String val2 = List2.get(0);
            if (!ignoreCase) {
                if (val1.compareTo(val2) < 0) {
                    ResultList.add(val1);
                    List1.remove(0);
                } else {
                    ResultList.add(val2);
                    List2.remove(0);
                }
            } else {
                if (val1.compareToIgnoreCase(val2) < 0) {
                    ResultList.add(val1);
                    List1.remove(0);
                } else {
                    ResultList.add(val2);
                    List2.remove(0);
                }
            }
        }
        if (!List1.isEmpty()) {
            ResultList.addAll(List1);
        }
        if (!List2.isEmpty()) {
            ResultList.addAll(List2);
        }
    }

    CopyOnWriteArrayList<String> getResult() {
        return ResultList;
    }

    public void showResults() {
        System.out.println("==============");
        for (String strLine : ResultList) {
            System.out.println(strLine);
        }

        System.out.println("==============");
    }
}
