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
                    inputData.close();
                    System.exit(0);
                } else {
                    try {
                        runner.parseAndExec(s);
                    } catch(ShellException e) {
                        if (e.getMessage().equals("exit")) {
                            inputData.close();
                            System.exit(0);
                        } else {
                            System.out.println(e.getMessage());
                            continue;
                        }
                    }
                }
            }
        } else {
            StringBuilder newComand = new StringBuilder();
            for (int i = 0; i < args.length; ++i) {
                newComand.append(args[i]);
                newComand.append(" ");
            }
            try {
                runner.parseAndExec(newComand.toString());
            } catch (ShellException e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}
