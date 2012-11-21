package ru.fizteh.fivt.students.frolovNikolay.parallelSort;

import java.util.ArrayList;
import java.util.Collections;

/*
 * Поток сортировки строк.
 */
public class StringSorter implements Runnable {
    private ArrayList<String> sortStation;
    private boolean withoutReg;
    StringSorter(ArrayList<String> sortStation, boolean withoutReg) {
        this.sortStation = sortStation;
        this.withoutReg = withoutReg;
    }

    // Супер примитив - собираем данные и сортируем на выходе.
    @Override
    public void run() {
        if (!withoutReg) {
            Collections.sort(sortStation);
        } else {
            Collections.sort(sortStation, String.CASE_INSENSITIVE_ORDER);
        }
    }
}
