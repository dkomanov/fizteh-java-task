package ru.fizteh.fivt.students.harius.chat.base;

public abstract class ConsoleObservable {
    ConsoleObserver observer = null;

    public final void setObserver(ConsoleObserver observer) {
        this.observer = observer;
    }

    public final void notifyObserver(String message) {
        if (observer != null) {
            observer.processConsole(message);
        }
    }
}