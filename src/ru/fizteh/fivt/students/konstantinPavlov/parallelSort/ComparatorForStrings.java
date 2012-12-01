package ru.fizteh.fivt.students.konstantinPavlov.parallelSort;

import java.util.Comparator;

public class ComparatorForStrings implements Comparator<String> {
    @Override
    public int compare(String one, String two) {
        return one.compareTo(two);
    }
}
