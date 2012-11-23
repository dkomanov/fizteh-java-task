package ru.fizteh.fivt.students.yuliaNikonova.parallelSort;

import java.util.Comparator;

public class StringComparator implements Comparator<String> {
private boolean ignoreCase;
    public int compare(String arg0, String arg1) {
        
            return arg0.compareTo(arg1);
        
    }

}
