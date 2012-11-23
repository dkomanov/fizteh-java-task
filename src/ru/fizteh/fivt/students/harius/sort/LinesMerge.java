package ru.fizteh.fivt.students.harius.sort;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class LinesMerge implements Callable<List<String>> {
    private List<String> li1;
    private List<String> li2;

    public LinesMerge(List<String> li1, List<String> li2) {
        this.li1 = li1;
        this.li2 = li2;
    }

    @Override
    public List<String> call() {
        List<String> result = new ArrayList<String>();
        int i1 = 0;
        int i2 = 0;
        while(i1 < li1.size() && i2 < li2.size()) {
            if (li1.get(i1).compareTo(li2.get(i2)) > 0) {
                result.add(li2.get(i2));
                ++i2;
            } else {
                result.add(li1.get(i1));
                ++i1;
            }
        }
        for ( ; i1 < li1.size(); ++i1) {
            result.add(li1.get(i1));
        }
        for ( ; i2 < li2.size(); ++i2) {
            result.add(li2.get(i2));
        }
        return result;
    }
}