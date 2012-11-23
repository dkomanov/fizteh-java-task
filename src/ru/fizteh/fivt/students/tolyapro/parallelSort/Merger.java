package ru.fizteh.fivt.students.tolyapro.parallelSort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.LinkedBlockingQueue;

public class Merger implements Runnable {
    LinkedBlockingQueue<ArrayList<String>> queue;
    Comparator<String> comparator;
    int number;
    ArrayList<Sorter> sorters;

    public Merger(Comparator<String> comparator,
            LinkedBlockingQueue<ArrayList<String>> queue,
            ArrayList<Sorter> sorters) {
        this.queue = queue;
        this.comparator = comparator;
        this.sorters = sorters;

    }

    @Override
    public void run() {
        ArrayList<String> result = new ArrayList<String>();
        while (true) {
            String min = null;
            Sorter sorter = null;
            for (int j = 0; j < sorters.size(); ++j) {
                if (sorters.get(j).hasNext()) {
                    String tmp = sorters.get(j).get();
                    if (min == null || comparator.compare(min, tmp) > 0) {
                        min = tmp;
                        sorter = sorters.get(j);
                    }
                }
            }
            if (min == null) {
                queue.clear();
                queue.add(result);
                return;
            } else {
                sorter.next();
                result.add(min);
            }
        }
    }
}
