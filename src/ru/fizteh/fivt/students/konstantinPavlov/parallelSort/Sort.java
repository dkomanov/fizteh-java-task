package ru.fizteh.fivt.students.konstantinPavlov.parallelSort;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Sort extends Thread {
    private final List<String> stringsToSort;
    private final Object synchronizer;
    private final LinkedBlockingQueue<PairOfIndexesToSort> linkedBlockingQueuee;
    private boolean flagNoRegister = false;

    public Sort(List<String> inputStringsToSort,
            LinkedBlockingQueue<PairOfIndexesToSort> inputLinkedBlockingQueuee,
            Object sync, boolean inputFlagNoRegister) {
        stringsToSort = inputStringsToSort;
        linkedBlockingQueuee = inputLinkedBlockingQueuee;
        synchronizer = sync;
        flagNoRegister = inputFlagNoRegister;
    }

    @Override
    public void run() {
        while (true) {
            PairOfIndexesToSort range = null;
            try {
                range = linkedBlockingQueuee.take();
                if (range.first == range.second) {
                    synchronized (synchronizer) {
                        if (linkedBlockingQueuee.isEmpty()) {
                            synchronizer.notify();
                        }
                    }
                    break;
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            if (flagNoRegister) {
                Collections.sort(
                        stringsToSort.subList(range.first, range.second),
                        String.CASE_INSENSITIVE_ORDER);
            } else {
                Collections.sort(stringsToSort.subList(range.first,
                        range.second));
            }
        }
    }
}
