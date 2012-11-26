package ru.fizteh.fivt.students.frolovNikolay.parallelSort;

import java.util.List;
import java.util.Collections;

/*
 * Поток сортировки строк.
 */
public class StringSorter implements Runnable {
    private List<String> sortStation;
    private boolean withoutReg;
    private int currentPtr;
    
    StringSorter(List<String> sortStation, boolean withoutReg, int left, int right) {
        this.sortStation = sortStation.subList(left, right);
        this.withoutReg = withoutReg;
        currentPtr = 0;
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
    
    public boolean hasValue() {
        return currentPtr != sortStation.size();
    }
    
    public void goNext() {
        ++currentPtr;
    }
    
    public String getValue() {
        return sortStation.get(currentPtr);
    }
}
