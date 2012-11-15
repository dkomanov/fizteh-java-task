package ru.fizteh.fivt.students.dmitriyBelyakov.chat.server;

import java.util.ArrayList;
import java.util.List;

class UserDeleteRegulator {
    private volatile boolean locked;
    private volatile List<User> forDelete;
    private final Manager manager;

    UserDeleteRegulator(Manager manager) {
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

    public void delete(User user) {
        if (locked) {
            forDelete.add(user);
        } else {
            manager.delete(user);
        }
    }

    private void deleteFromList() {
        for (User user : forDelete) {
            manager.delete(user);
        }
        forDelete.clear();
    }

    public void unlock() {
        locked = false;
        deleteFromList();
    }
}
