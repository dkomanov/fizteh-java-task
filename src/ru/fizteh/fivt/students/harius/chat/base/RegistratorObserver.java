package ru.fizteh.fivt.students.harius.chat.base;

import java.net.Socket;

public interface RegistratorObserver {
    void processRegistration(Socket socket);
}