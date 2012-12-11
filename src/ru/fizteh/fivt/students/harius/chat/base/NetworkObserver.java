package ru.fizteh.fivt.students.harius.chat.base;

import ru.fizteh.fivt.students.harius.chat.io.Packet;

public interface NetworkObserver {
    void processNetwork(Packet packet, NetworkObservable caller);
}