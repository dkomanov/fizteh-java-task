package ru.fizteh.fivt.students.almazNasibullin.parallelsort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

/**
 * 11.11.12
 * @author almaz
 */

public class Merger implements Runnable {
    List<String> res;
    List<List<String> > result;
    int start;
    int end;
    boolean withoutReg;
    
    public Merger (List<String> res, List<List<String> > result,int start, int end,
            boolean withoutReg){
        this.res = res;
        this.result = result;
        this.start = start;
        this.end = end;
        this.withoutReg = withoutReg;
    }
    
    @Override
    public void run() {
        TreeMap<String, List<Pair> > tm;
        if (withoutReg) {
            tm = new TreeMap<String, List<Pair> >(new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    String s1 = (String)o1;
                    String s2 = (String)o2;
                    if (s1.equals(s2)) {
                        return 0;
                    }
                    if (s1.compareToIgnoreCase(s2) == 0) {
                        return s1.compareTo(s2);
                    }
                    return s1.compareToIgnoreCase(s2);
                }
            });
        } else {
            tm = new TreeMap<String, List<Pair> >();
        }

        for (int i = start; i <= end; ++i) {
            String cur = result.get(i).get(0);
            if (!tm.containsKey(cur)) {
                tm.put(cur, new ArrayList<Pair>());
            }
            tm.get(cur).add(new Pair(i, 0));
        }
        while (!tm.isEmpty()) {
            removeAndAdd(result, res, tm);
        }
        tm.clear();
    }
    
    public void removeAndAdd(List<List<String> > result, List<String> res,
            TreeMap<String, List<Pair> > tm) {
        String cur = tm.firstKey();
        res.add(cur);
        Pair p = tm.get(cur).get(0);
        tm.get(cur).remove(p);
        if (tm.get(cur).isEmpty()) {
            tm.remove(cur);
        }
        if (p.second + 1 < result.get(p.first).size()) {
            String toAdd = result.get(p.first).get(p.second + 1);
            if (!tm.containsKey(toAdd)) {
                tm.put(toAdd, new ArrayList<Pair>());
            }
            tm.get(toAdd).add(new Pair(p.first, p.second + 1));
        }
    }
}
