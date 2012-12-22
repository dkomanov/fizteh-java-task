/*
 * LaunchClientGUI.java
 * Dec 15, 2012
 * By github.com/harius
 */

package ru.fizteh.fivt.students.harius.chat.launch;

import ru.fizteh.fivt.students.harius.chat.impl.Client;
import ru.fizteh.fivt.students.harius.chat.impl.displays.Gui;
import javax.swing.JOptionPane;

public class LaunchClientGui {
    public static void main(String[] args) {
        Object name = JOptionPane.showInputDialog(null,
                "Enter your nickname: ", 
                "Chat", JOptionPane.PLAIN_MESSAGE,
                null, null, "");
        if (name != null) {
            String nick = name.toString();
            if (!nick.matches("\\w{3,24}")) {
                JOptionPane.showMessageDialog(null,
                    "Incorrect nickname");
            } else {
                new LaunchClientGui().launch(nick);
            }
        }
    }

    public void launch(String name) {
        new Client(new Gui(), name);
    }
}