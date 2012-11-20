package ru.fizteh.fivt.students.frolovNikolay.parallelSort;

import java.util.LinkedList;
import java.util.List;

public class Merger {
    // Продолжаем попарно сливать массивы до тех пор пока не останеться один.
    static public void merge(List< LinkedList<String> > sortedLists, boolean withoutReg) throws Exception {
        while (sortedLists.size() != 1) {
            specialMerge(sortedLists, withoutReg);
        }
    }
    
    // сливание двух массивов.
    static private void specialMerge(List< LinkedList<String> > sortedLists, boolean withoutReg) throws Exception {
        LinkedList<String> mergedLists = new LinkedList<String>();
        int firstIdx = 0;
        int firstSize = sortedLists.get(0).size();
        int secondIdx = 0;
        int secondSize = sortedLists.get(1).size();
        boolean notFinish = true;
        while (notFinish) {
            if (firstIdx < firstSize && secondIdx < secondSize) {
                if (withoutReg) {
                    if (sortedLists.get(0).get(firstIdx).compareToIgnoreCase(sortedLists.get(1).get(secondIdx)) < 0) {
                        mergedLists.add(sortedLists.get(0).get(firstIdx++));
                    } else {
                        mergedLists.add(sortedLists.get(1).get(secondIdx++));
                    }
                } else {
                    if (sortedLists.get(0).get(firstIdx).compareTo(sortedLists.get(1).get(secondIdx)) < 0) {
                        mergedLists.add(sortedLists.get(0).get(firstIdx++));
                    } else {
                        mergedLists.add(sortedLists.get(1).get(secondIdx++));
                    }
                }
            } else if (firstIdx < firstSize) {
                mergedLists.add(sortedLists.get(0).get(firstIdx++));
            } else if (secondIdx < secondSize) {
                mergedLists.add(sortedLists.get(1).get(secondIdx++));
            } else {
                notFinish = false;
            }
        }
        sortedLists.remove(0);
        sortedLists.remove(0);
        sortedLists.add(mergedLists);
    }
}