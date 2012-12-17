/*
 * NetworkObservable.java
 * Dec 15, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.base;

import ru.fizteh.fivt.students.harius.chat.io.Packet;
import java.io.IOException;
import java.io.Closeable;

public abstract class NetworkObservable implements Closeable, Runnable {
    private NetworkObserver observer = null;
    private String name = null;

    public final void setObserver(NetworkObserver observer) {
        this.observer = observer;
    }

    public final void notifyObserver(Packet packet) {
        if (observer != null) {
            observer.processNetwork(packet, this);
        }
    }

    public final void notifyClosed(String message) {
        observer.processClosed(message, this);
    }

    public final String name() {
        return name;
    }

    public final void setName(String newName) {
        name = newName;
    }

    public abstract void send(Packet packet) throws IOException;
    public abstract String repr();
}