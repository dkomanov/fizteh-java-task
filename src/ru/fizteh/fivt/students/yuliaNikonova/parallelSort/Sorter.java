package ru.fizteh.fivt.students.yuliaNikonova.parallelSort;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Sorter extends Thread {
    private List<String> strList;
    private Comparator<String> stringComp;
    private int size;

    public Sorter(List<String> subList, Comparator<String> strComp) {
        strList = subList;
        stringComp = strComp;
        size=subList.size();
    }

    public void run() {
        // System.out.println("Size of sublist: " + strList.size());
        Collections.sort(strList, stringComp);
    }

    public List<String> getResult() {
        return strList;
    }

    /* public void showResults() {
     * for (String str : strList) {
     * System.out.println(str);
     * }
     * } */
}