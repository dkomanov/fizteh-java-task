package ru.fizteh.fivt.students.nikitaAntonov.parallelsort;

import java.util.List;

abstract class Sorter {
    protected ProgramOptions opts;
    
    public Sorter(ProgramOptions o) {
        opts = o;
    }
    
    abstract public List<String> readAndSort();
}
