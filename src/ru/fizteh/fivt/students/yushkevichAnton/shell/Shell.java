/*V 1.1, to understand that all is right*/
package ru.fizteh.fivt.students.yushkevichAnton.shell;

import java.io.*;

public class Shell {
    public static void main(String[] args) {
        if (args.length == 0) {
            new InteractiveShell().run();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : args) {
                stringBuilder.append(s);
                stringBuilder.append(" ");
            }

            try {
                Action action = CommandFactory.createAction(stringBuilder.toString(), new MovableFile("."));
                if (!action.execute()) {
                    System.exit(1);
                }
            } catch (IOException e) {
                System.err.println("File system inaccessible.");
                System.exit(1);
            }
        }
    }
}