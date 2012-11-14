package ru.fizteh.fivt.students.harius.sort;

import java.util.List;
import java.util.Collections;
import java.util.concurrent.Callable;

public class LinesSort implements Callable<List<String>> {
    private List<String> data;

    public LinesSort(List<String> data) {
        this.data = data;
    }

    @Override
    public List<String> call() {
        Collections.sort(data);
        return data;
    }
}