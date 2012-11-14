package ru.fizteh.fivt.students.harius.sort;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class LinesMerge implements Callable<List<String>> {
    private List<List<String>> datas;

    public LinesMerge(List<List<String>> datas) {
        this.datas = datas;
    }

    @Override
    public List<String> call() {
        List<String> result = new ArrayList<String>();
        int[] indices = new int[datas.size()];
        for(;;) {
            int best = -1;
            for (int array = 0; array < datas.size(); ++array) {
                if (indices[array] != datas.get(array).size()) {
                    String peek = datas.get(array).get(indices[array]);
                    if (best == -1 || peek.compareTo(datas.get(best).get(indices[best])) < 0) {
                        best = array;
                    }
                }
            }
            if (best == -1) {
                break;
            }
            result.add(datas.get(best).get(indices[best]));
            ++indices[best];
        }
        return result;
    }
}