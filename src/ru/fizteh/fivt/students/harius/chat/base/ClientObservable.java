/*
 * ConsoleObservable.java
 * Dec 21, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.base;

public abstract class ClientObservable {
    ClientObserver observer = null;

    public final void setObserver(ClientObserver observer) {
        this.observer = observer;
    }

    public final void notifyServerAdded(String name) {
        if (observer != null) {
            observer.processServerAdded(name);
        }
    }

    public final void notifyServerRemoved(int index) {
        if (observer != null) {
            observer.processServerRemoved(index);
        }
    }

    public final void notifyServerChanged(int index) {
        if (observer != null) {
            observer.processServerChanged(index);
        }
    }
}