package ru.fizteh.fivt.students.fedyuninV.wordCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class ResultContainer {

    private TreeMap<String, Integer> container = null;


    public ResultContainer(boolean ignoreCase) {
        if (ignoreCase) {
            container = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
        } else {
            container = new TreeMap<String, Integer>();
        }
    }

    public void add(ResultContainer given) {
        if (given == null) {
            return;
        }
        for (Map.Entry<String, Integer> entry: given.container.entrySet()) {
            if (container.get(entry.getKey()) != null) {
                container.put(entry.getKey(), entry.getValue() + entry.getValue());
            } else {
                container.put(entry.getKey(), entry.getValue());
            }
        }

    }

    public void add(String word) {
        Integer currCount = container.get(word);
        if (currCount != null) {
            container.put(word, currCount + 1);
        } else {
            container.put(word, 1);
        }
    }

    public void print(boolean unique) {
        if (unique) {
            for (Map.Entry<String, Integer> entry: container.entrySet()) {
                System.out.println(entry.getKey() + ' ' + entry.getValue());
            }
        } else {
            int result = 0;
            for (Map.Entry<String, Integer> entry: container.entrySet()) {
                result += entry.getValue();
            }
            System.out.println(result);
        }
    }

}
