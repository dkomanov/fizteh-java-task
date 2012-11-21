package ru.fizteh.fivt.students.frolovNikolay.parallelSort;

import java.util.ArrayList;

public class Merger {
    private ArrayList< ArrayList<String> > data;
    boolean withoutReg;
    private int [] currentPtr;
    
    Merger(ArrayList< ArrayList<String> > inputData, boolean inputWithoutReg) {
        data = inputData;
        withoutReg = inputWithoutReg;
        currentPtr = new int[data.size()];
    }

    public String getNext() {
        String min = null;
        int numberOfMin = -1;
        for (int i = 0; i < data.size(); ++i) {
            if (currentPtr[i] < data.get(i).size()) {
                if (min == null) {
                    min = data.get(i).get(currentPtr[i]);
                    numberOfMin = i;
                } else if (withoutReg && min.compareToIgnoreCase(data.get(i).get(currentPtr[i])) < 0) {
                    min = data.get(i).get(currentPtr[i]);
                    numberOfMin = i;
                } else if (!withoutReg && min.compareTo(data.get(i).get(currentPtr[i])) < 0) {
                    min = data.get(i).get(currentPtr[i]);
                    numberOfMin = i;
                }
            }
        }
        if (min != null) {
            ++currentPtr[numberOfMin];
        }
        return min;
    }
}