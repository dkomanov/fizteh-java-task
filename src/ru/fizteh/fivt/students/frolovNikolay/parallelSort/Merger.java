package ru.fizteh.fivt.students.frolovNikolay.parallelSort;

import java.util.ArrayList;

public class Merger {
    private ArrayList<StringSorter> sorters;
    boolean withoutReg;
    
    Merger(ArrayList<StringSorter> sorters, boolean withoutReg) {
        this.sorters = sorters;
        this.withoutReg = withoutReg;
    }

    public String getNext() {
        String min = null;
        int numberOfMin = -1;
        for (int i = 0; i < sorters.size(); ++i) {
            if (sorters.get(i).hasValue()) {
                if (min == null) {
                    min = sorters.get(i).getValue();
                    numberOfMin = i;
                } else if (!withoutReg && min.compareTo(sorters.get(i).getValue()) > 0) {
                    min = sorters.get(i).getValue();
                    numberOfMin = i;
                } else if (withoutReg && min.compareToIgnoreCase(sorters.get(i).getValue()) > 0) {
                    min = sorters.get(i).getValue();
                    numberOfMin = i;
                }
            }
        }
        if (numberOfMin != -1) {
            sorters.get(numberOfMin).goNext();
        }
        return min;
    }
}