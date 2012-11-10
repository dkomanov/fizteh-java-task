package ru.fizteh.fivt.students.fedyuninV.parallelSort;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class Sorter implements Runnable{

    List<String> container = null;
    ResultContainer finish = null;
    boolean ignoreCase;

    public Sorter(ResultContainer finish, List<String> container, boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        this.finish = finish;
        this.container = container;
    }


    public void run() {
        if (ignoreCase) {
            Collections.sort(container, String.CASE_INSENSITIVE_ORDER);
        } else {
            Collections.sort(container);
        }
        synchronized (finish) {
            finish.add(new ResultContainer(ignoreCase, container));
        }
    }
}
