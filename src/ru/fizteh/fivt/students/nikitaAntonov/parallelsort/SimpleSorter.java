package ru.fizteh.fivt.students.nikitaAntonov.parallelsort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class SimpleSorter extends Sorter {
    
    Comparator<String> cmp;
    
    public SimpleSorter(ProgramOptions o) {
        super(o);
        cmp = o.caseInsensitive ? String.CASE_INSENSITIVE_ORDER : new DefaultComparator();
    }

    @Override
    public List<String> readAndSort() throws IOException {
        
        ArrayList<String> data = opts.getChunk();
        
        if (data.isEmpty()) {
            return data;
        }
        
        Collections.sort(data, cmp);
        
        if (!opts.unique) {
            return data;
        }
        
        ArrayList<String> result = new ArrayList<>(data.size());
        
        result.add(data.get(0));
        for (int i = 1, e = data.size(); i < e; ++i) {
            if (cmp.compare(data.get(i), data.get(i - 1)) != 0) {
                result.add(data.get(i));
            }
        }
        
        data.clear();
        
        return result;
    }

}
