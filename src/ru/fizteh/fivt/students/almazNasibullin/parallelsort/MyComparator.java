package ru.fizteh.fivt.students.almazNasibullin.parallelsort;

import java.util.Comparator;

/**
 * 29.10.12
 * @author almaz
 */

public class MyComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        return ((String)o1).toLowerCase().compareTo(((String)o2).toLowerCase());
    }
}
