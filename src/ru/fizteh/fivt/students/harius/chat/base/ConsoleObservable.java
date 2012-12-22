/*
 * ConsoleObservable.java
 * Dec 15, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.base;

import java.io.Closeable;

public abstract class ConsoleObservable implements Runnable, Closeable {
    ConsoleObserver observer = null;

    public final void setObserver(ConsoleObserver observer) {
        this.observer = observer;
    }

    public final void notifyObserver(String message) {
        if (observer != null) {
            observer.processConsole(message);
        }
    }

    public final void notifyClosed() {
        if (observer != null) {
            observer.processClosed();
        }
    }
}