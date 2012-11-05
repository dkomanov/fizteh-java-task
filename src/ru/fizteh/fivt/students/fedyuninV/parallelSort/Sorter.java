package ru.fizteh.fivt.students.fedyuninV.parallelSort;

import java.util.ArrayList;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class Sorter implements Runnable{

    ArrayList<String> container = null;
    ResultContainer finish = null;
    boolean ignoreCase;

    public Sorter(ResultContainer finish, ArrayList<String> container, boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        this.finish = finish;
        this.container = container;
    }


    public void run() {
        ResultContainer result = new ResultContainer(ignoreCase);
        while (!container.isEmpty()) {
            result.add(container.remove(container.size() - 1));
        }
        synchronized (finish) {
            finish.add(result);
        }
    }
}
