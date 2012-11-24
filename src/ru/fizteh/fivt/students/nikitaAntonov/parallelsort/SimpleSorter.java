package ru.fizteh.fivt.students.nikitaAntonov.parallelsort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SimpleSorter extends Sorter {
    
    public SimpleSorter(ProgramOptions o) {
        super(o);
    }

    @Override
    public List<String> readAndSort() {
        
        ArrayList<String> data = opts.getChunk();
        
        Collections.sort(data);
        
        return data;
    }

}
