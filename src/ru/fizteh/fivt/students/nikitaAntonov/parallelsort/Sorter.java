package ru.fizteh.fivt.students.nikitaAntonov.parallelsort;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

abstract class Sorter {
    protected ProgramOptions opts;
    protected Comparator<String> cmp;

    public Sorter(ProgramOptions o) {
        opts = o;
        cmp = o.caseInsensitive ? String.CASE_INSENSITIVE_ORDER
                : new DefaultComparator();
    }

    abstract public List<String> readAndSort() throws IOException,
            InterruptedException;
}
