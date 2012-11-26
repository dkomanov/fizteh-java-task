package ru.fizteh.fivt.students.tolyapro.shell;

import java.util.Scanner;

public class Shell {

    public static String execute(String[] arguments,
            ShellCommandsExecutor shell, String currPath,
            boolean isModeInteractive) {
        for (String arg : arguments) {
            try {
                if (!arg.isEmpty()) {
                    arg = arg.replaceAll("^\\s+", "");
                    arg = arg.replaceAll("$\\s+", "");
                    currPath = shell.execute(arg, currPath);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                if (!isModeInteractive) {
                    System.exit(1);
                }

            }
        }
        return currPath;
    }

    public static void main(String[] args) {
        ShellCommandsExecutor shell = new ShellCommandsExecutor();
        String currPath = System.getProperty("user.dir");
        if (args.length > 0) {
            StringBuilder argumentsBuilder = new StringBuilder();
            for (String s : args) {
                argumentsBuilder.append(s);
                argumentsBuilder.append(" ");
            }
            String[] arguments = argumentsBuilder.toString().split(";");
            currPath = execute(arguments, shell, currPath, false);
        } else {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("$ ");
                try {
                    String input = scanner.nextLine();
                    if (input != null) {
                        String[] arguments = input.split(";");
                        currPath = execute(arguments, shell, currPath, true);
                    } else {
                        System.exit(0);
                    }

                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    System.exit(1);
                }
            }
        }
    }
}
