package ru.fizteh.fivt.students.harius.sort;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.Comparator;

public class LinesMerge implements Runnable {
    private List<String> li1;
    private List<String> li2;
    private List<List<String>> q;
    private int index;
    private Comparator<String> comp;

    public LinesMerge(List<String> li1, List<String> li2,
        List<List<String>> q, int index, Comparator<String> comp) {

        this.li1 = li1;
        this.li2 = li2;
        this.q = q;
        this.index = index;
        this.comp = comp;
    }

    @Override
    public void run() {
        if (li2 == null) {
            q.set(index, li1);
            return;
        }
        
        List<String> result = new ArrayList<String>();
        int i1 = 0;
        int i2 = 0;
        while(i1 < li1.size() && i2 < li2.size()) {
            if (comp.compare(li1.get(i1), li2.get(i2)) > 0) {
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
        q.set(index, result);
    }
}