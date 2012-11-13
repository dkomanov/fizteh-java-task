package misc.shell;

import java.io.*;

public class Shell {
    public static void main(String[] args) {
        if (args.length == 0) {
            new InteractiveShell().run();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (String s : args) {
                stringBuilder.append(s);
            }

            Action action = CommandFactory.createAction(stringBuilder.toString(), new MovableFile("."));
            action.execute();
        }
    }
}