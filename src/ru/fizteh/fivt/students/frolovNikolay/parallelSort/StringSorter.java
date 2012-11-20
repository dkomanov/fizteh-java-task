package ru.fizteh.fivt.students.frolovNikolay.parallelSort;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * Поток сортировки строк.
 */
public class StringSorter implements Runnable {
    private LinkedBlockingQueue<String> resourses;
    private LinkedList<String> sortStation;
    private List< LinkedList<String> > finalContainer;
    private boolean withoutReg;
    private String EOF;
    StringSorter(String _EOF, LinkedBlockingQueue<String> _resourses, boolean _withoutReg,
                 List< LinkedList<String> > _finalContainer) {
        resourses = _resourses;
        sortStation = new LinkedList<String>();
        finalContainer = _finalContainer;
        withoutReg = _withoutReg;
        EOF = _EOF;
    }

    // Супер примитив - собираем данные и сортируем на выходе.
    @Override
    public void run() {
        String current = null;
        do {
            try {
                current = resourses.take();
            } catch (Exception crush) {
                System.err.println(crush.getMessage());
                System.exit(1);
            }
            if (current != EOF) {
                sortStation.add(current);
            } else {
                break;
            }
        } while (true);
        if (!withoutReg) {
            Collections.sort(sortStation);
        } else {
            Collections.sort(sortStation, String.CASE_INSENSITIVE_ORDER);
        }
        finalContainer.add(sortStation);
    }
}
