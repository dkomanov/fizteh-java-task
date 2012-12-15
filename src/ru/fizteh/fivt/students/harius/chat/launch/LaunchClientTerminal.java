/*
 * LaunchClientTerminal.java
 * Dec 15, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.launch;

import ru.fizteh.fivt.students.harius.chat.impl.Client;
import ru.fizteh.fivt.students.harius.chat.impl.displays.Terminal;

public class LaunchClientTerminal {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Please provide your nickname");
        } else {
            new LaunchClientTerminal().launch(args[0]);
        }
    }

    public void launch(String name) {
        new Client(new Terminal(), name);
    }
}