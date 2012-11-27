package ru.fizteh.fivt.students.nikitaAntonov.parallelsort;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

abstract class Sorter {
    protected ProgramOptions opts;
    protected Comparator<Line> cmp;

    public Sorter(ProgramOptions o) {
        opts = o;
        cmp = new LineComparator(o.caseInsensitive, o.unique);
    }

    abstract public List<Line> readAndSort() throws IOException,
            InterruptedException;
}

class LineComparator implements Comparator<Line> {
    private Comparator<String> comparator;
    private boolean isUnique;
    
    public LineComparator(boolean caseInsensitive, boolean unique) {
        comparator = caseInsensitive ? String.CASE_INSENSITIVE_ORDER
                : new DefaultComparator();
        isUnique = unique;
    }
    
    @Override
    public int compare(Line o1, Line o2) {
        int result = comparator.compare(o1.str, o2.str);
        if (result == 0 && !isUnique) {
            return o1.chunkNo - o2.chunkNo;
        } else {
            return result;
        }
    }
   
}