package ru.fizteh.fivt.students.yuliaNikonova.parallelSort;

import java.util.List;

public class Merger extends Thread {
    private List<String> List1;
    private List<String> List2;
    private List<String> ResultList;
    private boolean ignoreCase;

    public Merger(List<String> List1, List<String> List2, boolean ignoreCase) {
        this.List1 = List1;
        this.List2 = List2;
        this.ignoreCase = ignoreCase;
    }

    public void run() {
        while (!List1.isEmpty() && !List2.isEmpty()) {
            String val1 = List1.get(0);
            String val2 = List2.get(0);
            if ((val1.compareTo(val2) > 0 && !ignoreCase) || (val1.compareToIgnoreCase(val2) > 0 && ignoreCase)) {
                ResultList.add(val1);
                List1.remove(0);
            } else {
                ResultList.add(val2);
                List2.remove(0);
            }
        }
        if (!List1.isEmpty()) {
            ResultList.addAll(List1);
        } else if (!List2.isEmpty()) {
            ResultList.addAll(List1);
        }
    }

    List<String> getResult() {
        return ResultList;
    }
}
