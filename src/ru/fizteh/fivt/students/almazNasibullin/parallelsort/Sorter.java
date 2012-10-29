package ru.fizteh.fivt.students.almazNasibullin.parallelsort;

import java.util.Collections;
import java.util.List;

/**
 * 23.10.12
 * @author almaz
 */

public class Sorter implements Runnable {
    List<String> lines;
    // входной массив
    int start; // индекс начала сортировки
    int end; // индекс конца сортировки
    List<String> result; // отсортированный массив строк
    boolean withoutReg;

    public Sorter(List<String> lines, int start, int end, List<String> result,
            boolean withoutReg) {
        this.lines = lines;
        this.start = start;
        this.end = end;
        this.result = result;
        this.withoutReg = withoutReg;
    }

    @Override
    public void run() {
        for (int i = start; i <= end; ++i) {
            result.add(lines.get(i));
        }
        if (withoutReg) {
            MyComparator myComparator = new MyComparator();
            Collections.sort(result, myComparator);
        } else {
            Collections.sort(result);
        }
    }

}
