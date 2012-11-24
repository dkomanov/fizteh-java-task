package ru.fizteh.fivt.students.nikitaAntonov.parallelsort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class SimpleSorter extends Sorter {
    
    public SimpleSorter(ProgramOptions o) {
        super(o);
    }

    @Override
    public List<Line> readAndSort() throws IOException {

        ArrayList<Line> data = opts.getChunk();
        
        if (data == null || data.isEmpty()) {
            return data;
        }

        Collections.sort(data, cmp);

        if (!opts.unique) {
            return data;
        }

        ArrayList<Line> result = new ArrayList<>(data.size());

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

