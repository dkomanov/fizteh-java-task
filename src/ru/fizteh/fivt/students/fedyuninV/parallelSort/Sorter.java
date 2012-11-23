package ru.fizteh.fivt.students.fedyuninV.parallelSort;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class Sorter implements Runnable{

    List<StringContainer> container = null;
    ResultContainer finish = null;
    boolean ignoreCase;

    public Sorter(ResultContainer finish, List<StringContainer> container, boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        this.finish = finish;
        this.container = container;
    }


    public void run() {
        if (ignoreCase) {
            Collections.sort(container, new StringContainer.CaseInsensitiveComparator());
        } else {
            Collections.sort(container, new StringContainer.DefaultComparator());
        }
        ResultContainer resultContainer = new ResultContainer(ignoreCase, container);
        synchronized (finish) {
            finish.add(resultContainer);
        }
    }
}
