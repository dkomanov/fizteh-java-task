package ru.fizteh.fivt.students.harius.chat.impl;

import ru.fizteh.fivt.students.harius.chat.base.*;

public final class Server {
    private final DisplayBase display;
    private ServerConsoleAdapter inputProcessor
        = new ServerConsoleAdapter() {
        
        @Override
        public void listen(int port) {

        }

        @Override
        public void stop() {

        }

        @Override
        public void list() {

        }

        @Override
        public void send(String user, String message) {

        }

        @Override
        public void sendall(String message) {

        }

        @Override
        public void kill(String user) {

        }

        @Override
        public void exit() {

        }
    };

    public Server(DisplayBase display) {
        this.display = display;
        display.setObserver(inputProcessor);
    }
}