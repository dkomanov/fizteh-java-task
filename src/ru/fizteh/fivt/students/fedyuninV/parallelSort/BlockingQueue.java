package ru.fizteh.fivt.students.fedyuninV.parallelSort;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Fedyunin Valeriy
 * MIPT FIVT 195
 */
public class BlockingQueue <E> {
    private LinkedList<E> queue = new LinkedList<E>();

    public void add(E newObject) {
        synchronized (queue) {
            queue.addLast(newObject);
            queue.notify();
        }
    }

    public E remove() {
        try {
            synchronized (queue) {
                while (queue.isEmpty()) {
                    queue.wait();
                }
                return queue.removeFirst();
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        return null;
    }

    public boolean isEmpty() {
        synchronized (queue) {
            return (queue.size() == 0);
        }
    }
}
