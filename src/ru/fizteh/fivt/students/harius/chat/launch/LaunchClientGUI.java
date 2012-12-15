/*
 * LaunchClientGUI.java
 * Dec 15, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.launch;

import ru.fizteh.fivt.students.harius.chat.impl.Client;
import ru.fizteh.fivt.students.harius.chat.impl.displays.GUI;

public class LaunchClientGUI {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Please provide your nickname");
        } else {
            new LaunchClientGUI().launch(args[0]);
        }
    }

    public void launch(String name) {
        new Client(new GUI(), name);
    }
}