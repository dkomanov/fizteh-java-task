package ru.fizteh.fivt.students.fedyuninV.parallelSort;

import java.util.ArrayList;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class StringSorter implements Runnable{
    private ArrayList<String> dataContainer;
    private ResultContainer result = null;
    private final ResultContainer finish;
    private Object startSem = new Object();
    private Object finishSem = new Object();

    public StringSorter (ArrayList<String> dataContainer, boolean ignoreCase,
                         Object startSem, Object finishSem, ResultContainer finish) {
        this.dataContainer = dataContainer;
        this.startSem = startSem;
        this.finishSem = finishSem;
        result = new ResultContainer(ignoreCase);
        this.finish = finish;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                finishSem.notify();
                startSem.wait();
                synchronized (dataContainer) {
                    for (int i = dataContainer.size() - 1; i >= 0; i--) {
                        result.add(dataContainer.remove(i));
                    }
                }
                synchronized (finish) {
                    finish.add(result);
                }
            } catch (Exception ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
    }
}
