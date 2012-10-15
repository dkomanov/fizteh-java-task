package ru.fizteh.fivt.students.dmitriyBelyakov.shell;

import ru.fizteh.fivt.students.dmitriyBelyakov.shell.CommandExecutor;

import java.io.*;
import java.util.ArrayList;

public class Shell {
    public static void main(String[] args) {
        String curDirPth = System.getProperty("user.dir");
        ArrayList<String> curDir = new ArrayList<String>();
        curDir.add(curDirPth);
        if (args.length != 0) {
            StringBuilder builder = new StringBuilder();
            for (String s : args) {
                builder.append(s);
                builder.append(" ");
            }
            CommandExecutor.executeCommands(builder.toString(), curDir, false);
        } else {
            while (true) {
                System.out.print("$ ");
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    String commands = reader.readLine();
                    CommandExecutor.executeCommands(commands, curDir, true);
                } catch (IOException e) {
                    System.err.println("Error: cannot read the command.");
                    System.exit(1);
                }
            }
        }
    }
}