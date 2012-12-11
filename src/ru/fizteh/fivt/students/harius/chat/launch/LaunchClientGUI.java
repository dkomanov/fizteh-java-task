package ru.fizteh.fivt.students.harius.chat.launch;

import ru.fizteh.fivt.students.harius.chat.impl.Client;
import ru.fizteh.fivt.students.harius.chat.impl.displays.GUI;

public class LaunchClientGUI {
    public static void main(String[] args) {
        new LaunchClientGUI().launch();
    }

    public void launch() {
        new Client(new GUI());
    }
}