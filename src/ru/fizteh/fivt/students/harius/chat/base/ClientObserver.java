/*
 * ClientObserver.java
 * Dec 21, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.base;

public interface ClientObserver {
    void processServerAdded(String input);
    void processServerRemoved(int index);
    void processServerChanged(int index);    
}