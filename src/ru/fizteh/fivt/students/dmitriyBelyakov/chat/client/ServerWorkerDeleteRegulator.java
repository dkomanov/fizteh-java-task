package ru.fizteh.fivt.students.dmitriyBelyakov.chat.client;

import java.util.ArrayList;
import java.util.List;

class ServerWorkerDeleteRegulator {
    private volatile boolean locked;
    private volatile List<ServerWorker> forDelete;
    private final Manager manager;

    ServerWorkerDeleteRegulator(Manager manager) {
        this.manager = manager;
        locked = false;
        forDelete = new ArrayList<>();
    }

    boolean isLocked() {
        return locked;
    }

    public void lock() {
        locked = true;
    }

    public void delete(ServerWorker server) {
        if (locked) {
            forDelete.add(server);
        } else {
            manager.delete(server);
        }
    }

    private void deleteFromList() {
        for (ServerWorker server : forDelete) {
            manager.delete(server);
        }
        forDelete.clear();
    }

    public void unlock() {
        locked = false;
        deleteFromList();
    }
}
