package ru.fizteh.fivt.students.yuliaNikonova.parallelSort;

import java.util.Comparator;

public class StringComparator implements Comparator<String>{
private boolean ignoreCase;
    public StringComparator(boolean ignoreCase) {
        this.ignoreCase=ignoreCase;
    }
    public int compare(String arg0, String arg1) {
        if (ignoreCase) {
            return String.CASE_INSENSITIVE_ORDER.compare(arg0, arg1);
        } else {
            return arg0.compareTo(arg1);
        }
    }

}
