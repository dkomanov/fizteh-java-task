/*V 1.1, to understand that all is right*/
package misc.shell;

import java.io.*;

public class InteractiveShell {
    private BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public void run() {
        MovableFile position = null;

        try {
            position = new MovableFile(".");
        } catch (IOException e) {
            System.err.println("File system inaccessible.");
            return;
        }

        while (true) {
            System.out.print(position.getFile().getAbsolutePath() + "$ ");

            String line = null;
            try {
                line = in.readLine();
            } catch (IOException e) {
                System.err.println("Reading error.");
                break;
            }
            if (line == null) {
                System.err.println("End of stream.");
                break;
            }

            Action action = CommandFactory.createAction(line, position);
            action.execute();
        }
    }
}