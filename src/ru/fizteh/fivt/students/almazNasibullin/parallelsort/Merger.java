package ru.fizteh.fivt.students.almazNasibullin.parallelsort;

import java.util.ArrayList;
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
    boolean withoutReg;
    
    public Merger (List<String> res, List<List<String> > result,int start, int end,
            boolean withoutReg){
        this.res = res;
        this.result = result;
        this.start = start;
        this.end = end;
        this.withoutReg = withoutReg;
    }

    public void run() {
        int left = 0;
        int right = 0;

        while (left < result.get(start).size() && right < result.get(end).size()) {
            if (withoutReg) {
                if (result.get(start).get(left).compareToIgnoreCase(
                        result.get(end).get(right)) <= 0) {
                    res.add(result.get(start).get(left));
                    ++left;
                } else {
                    res.add(result.get(end).get(right));
                    ++right;
                }
            } else {
                if (result.get(start).get(left).compareTo(
                        result.get(end).get(right)) <= 0) {
                    res.add(result.get(start).get(left));
                    ++left;
                } else {
                    res.add(result.get(end).get(right));
                    ++right;
                }
            }
        }

        for (int i = left; i < result.get(start).size(); ++i) {
            res.add(result.get(start).get(i));
        }
        for (int i = right; i < result.get(end).size(); ++i) {
            res.add(result.get(end).get(i));
        }
    }
}
