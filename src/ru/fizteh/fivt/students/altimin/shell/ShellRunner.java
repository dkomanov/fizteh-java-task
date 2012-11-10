package ru.fizteh.fivt.students.altimin.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * User: altimin
 * Date: 11/10/12
 * Time: 4:00 PM
 */
public class ShellRunner {

    static final String greeting = "$ ";

    public static void runPackageMode(String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i ++) {
            if (i > 0) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(args[i]);
        }
        Shell shell = new Shell();
        try {
            shell.processCommands(stringBuilder.toString());
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.toString());
            System.exit(1);
        } catch (InterruptedException exception) {
            return;
        }

    }

    public static void runInteractiveMode() throws IOException {
        Shell shell = new Shell();
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print(greeting);
            String command = inputStream.readLine();
            try {
                shell.processCommands(command);
            } catch (IllegalArgumentException exception) {
                System.err.println(exception.toString());
            } catch (InterruptedException exception) {
                return;
            }
        }
    }


    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            runInteractiveMode();
        } else {
            runPackageMode(args);
        }
    }
}
