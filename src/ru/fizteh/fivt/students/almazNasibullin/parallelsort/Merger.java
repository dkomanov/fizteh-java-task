package ru.fizteh.fivt.students.almazNasibullin.parallelsort;

import java.util.Comparator;
import java.util.List;
/**
 * 11.11.12
 * @author almaz
 */

public class Merger implements Runnable {
    List<String> res;
    List<List<String> > result;
    int start;
    int end;
    int from1;
    int from2;
    int to1;
    int to2;
    Comparator<String> com;
    
    public Merger (List<String> res, List<List<String> > result, int start, int end,
            int from1, int to1, int from2, int to2, boolean withoutReg){
        this.res = res;
        this.result = result;
        this.start = start;
        this.end = end;
        this.from1 = from1;
        this.to1 = to1;
        this.from2 = from2;
        this.to2 = to2;
        if (withoutReg) {
            com = String.CASE_INSENSITIVE_ORDER;
        } else {
            com = new Comparator<String> () {
                public int compare(String s1, String s2) {
                    return s1.compareTo(s2);
                }
            };
        }
    }

    @Override
    public void run() {
        while (from1 <= to1 && from2 <= to2) {
            if (com.compare(result.get(start).get(from1), result.get(end).get(from2)) <= 0) {
                res.add(result.get(start).get(from1));
                ++from1;
            } else {
                res.add(result.get(end).get(from2));
                ++from2;
            }
        }

        for (int i = from1; i <= to1; ++i) {
            res.add(result.get(start).get(i));
        }
        for (int i = from2; i <= to2; ++i) {
            res.add(result.get(end).get(i));
        }
    }
}
