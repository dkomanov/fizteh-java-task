package ru.fizteh.fivt.students.harius.chat.base;

import ru.fizteh.fivt.students.harius.chat.io.Packet;

public abstract class NetworkObservable {
    NetworkObserver observer = null;

    public final void setObserver(NetworkObserver observer) {
        this.observer = observer;
    }

    public final void notifyObserver(Packet packet) {
        if (observer != null) {
            observer.processNetwork(packet, this);
        }
    }
}