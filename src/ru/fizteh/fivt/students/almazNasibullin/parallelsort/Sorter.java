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

    public Sorter(List<String> lines, int start, int end, List<String> result) {
        this.lines = lines;
        this.start = start;
        this.end = end;
        this.result = result;
    }

    @Override
    public void run() {
        for (int i = start; i <= end; ++i) {
            result.add(lines.get(i));
        }
        Collections.sort(result);
    }

}
