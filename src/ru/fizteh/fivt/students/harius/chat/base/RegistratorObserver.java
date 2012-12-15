/*
 * RegistratorObserver.java
 * Dec 15, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.base;

import java.net.Socket;

public interface RegistratorObserver {
    void processRegistration(Socket socket);
}