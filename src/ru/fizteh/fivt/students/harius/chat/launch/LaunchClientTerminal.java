package ru.fizteh.fivt.students.harius.chat.launch;

import ru.fizteh.fivt.students.harius.chat.impl.Client;
import ru.fizteh.fivt.students.harius.chat.impl.displays.Terminal;

public class LaunchClientTerminal {
    public static void main(String[] args) {
        new LaunchClientTerminal().launch();
    }

    public void launch() {
        new Client(new Terminal());
    }
}