/*
 * Display.java
 * Dec 15, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.base;

public interface Display {
    void message(String nickname, String message);
    void warn(String warning);
    void error(String error);
    boolean needsReflect();
}