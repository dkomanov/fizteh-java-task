package ru.fizteh.fivt.students.harius.chat.impl;

import ru.fizteh.fivt.students.harius.chat.base.*;

public final class Client {
    private final DisplayBase display;
    private ClientConsoleAdapter inputProcessor
        = new ClientConsoleAdapter() {
        
        @Override
        public void connect(String host, int port) {

        }

        @Override
        public void disconnect() {

        }

        @Override
        public void whereami() {

        }

        @Override
        public void list() {

        }

        @Override
        public void use(String server) {

        }

        @Override
        public void exit() {

        }

        @Override
        public void sendMessage(String message) {

        }
    };

    public Client(DisplayBase display) {
        this.display = display;
        display.setObserver(inputProcessor);
    }
}