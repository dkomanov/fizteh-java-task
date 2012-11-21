package ru.fizteh.fivt.students.yuliaNikonova.parallelSort;

import java.util.Collections;
import java.util.List;

public class Sorter extends Thread {
    private List<String> strList;
    private StringComparator stringComp;

    public Sorter(List<String> subList, StringComparator strComp) {
        strList = subList;
               stringComp=strComp;
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