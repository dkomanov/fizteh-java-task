package ru.fizteh.fivt.students.harius.chat.launch;

import ru.fizteh.fivt.students.harius.chat.impl.Server;
import ru.fizteh.fivt.students.harius.chat.impl.displays.Terminal;

public class LaunchServer {
    public static void main(String[] args) {
        new LaunchServer().launch();
    }

    public void launch() {
        new Server(new Terminal());
    }
}