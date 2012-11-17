package ru.fizteh.fivt.students.yuliaNikonova.parallelSort;

import java.util.Collections;
import java.util.List;

public class Sorter extends Thread {
    private List<String> strList;
    private boolean ignoreCase;

    public Sorter(List<String> subList, boolean ignoreCase) {
        strList = subList;
        this.ignoreCase = ignoreCase;
    }

    @Override
    public void run() {
        // System.out.println("Size of sublist: " + strList.size());
        if (ignoreCase) {
            Collections.sort(strList, String.CASE_INSENSITIVE_ORDER);
        } else {
            Collections.sort(strList);
        }

    }

    public List<String> getResult() {
        return strList;
    }

    public void showResults() {
        System.out.println("============");
        for (String str : strList) {
            System.out.println(str);
        }
        System.out.println("============");
    }
}