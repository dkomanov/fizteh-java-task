package ru.fizteh.fivt.students.mysinYurii.shell;

import java.util.Scanner;

public class Shell {
    public static void main(String[] args) {
        Executor runner = new Executor();
        if (args.length == 0) {
            Scanner inputData = new Scanner(System.in);
            while (true) {
                System.out.print("$ ");
                String s = inputData.nextLine();
                if (s == null) {
                    System.exit(0);
                } else {
                    try {
                        runner.parseAndExec(s);
                    } catch(ShellException e) {
                        if (e.getMessage().equals("exit")) {
                            System.exit(0);
                        } else {
                            System.out.println(e.getMessage());
                            continue;
                        }
                    }
                }
            }
        } else {
            StringBuilder newCommand = new StringBuilder();
            for (int i = 0; i < args.length; ++i) {
                newCommand.append(args[i]);
                newCommand.append(" ");
            }
            String[] commandArray = newCommand.toString().split(";");
            for (int i = 0; i < commandArray.length; ++i) {
                commandArray[i].trim();
                try {
                    runner.parseAndExec(commandArray[i]);
                } catch (ShellException e) {
                    System.out.println(e.getMessage());
                    System.exit(1);
                }
            }
        }
    }
}
